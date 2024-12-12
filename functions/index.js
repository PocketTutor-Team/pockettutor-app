const { onSchedule } = require("firebase-functions/v2/scheduler");
const { setGlobalOptions } = require("firebase-functions/v2");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const logger = require("firebase-functions/logger");
const { onDocumentUpdated } = require("firebase-functions/v2/firestore");
const { notifyStudentLessonConfirmedByTutor, notifyTutorForConfirmation, notifyStudentLessonPendingReview, notifyStudentAsTutorOfferedToTeach} = require("./lessonNotifications");


initializeApp();

setGlobalOptions({
    maxInstances: 10,
    timeoutSeconds: 60,
    memory: "256MiB"
});

//Send notification when a lesson is updated
exports.lessonStatusChanged = onDocumentUpdated("lessons/{lessonId}", async (event) => {
  const before = event.data.before.data();
  const after = event.data.after.data();

  if (before.status !== after.status) {
    console.log(`Lesson status changed from ${before.status} to ${after.status}`);

    if (before.status === "PENDING_TUTOR_CONFIRMATION" && after.status === "CONFIRMED") {
      await notifyStudentLessonConfirmedByTutor(after);
    }

    if (before.status === "MATCHING" && after.status === "PENDING_TUTOR_CONFIRMATION") {
      await notifyTutorForConfirmation(after);
    }
    // Notify student when lesson is pending review
    if (before.status === "CONFIRMED" && after.status === "PENDING_REVIEW") {
      await notifyStudentLessonPendingReview(after);
    }
  }

  //Detect when an additional tutor offer to teach a requested lesson:
  //more specifically when a tutor uid is added to the tutorUid list of a STUDENT_REQUESTED lesson
  if (after.status === "STUDENT_REQUESTED" && before.tutorUid.length < after.tutorUid.length) {
    console.log("Tutor added to the lesson's tutorUid.");
    await notifyStudentAsTutorOfferedToTeach(after);
  }
});

//Check if lessons are completed and update their status accordingly
exports.checkCompletedLessons = onSchedule("* * * * *", async (event) => {
    try {
        const now = new Date();
        const db = getFirestore();

        // Fetch lessons that are either CONFIRMED or INSTANT_CONFIRMED
        const snapshot = await db.collection('lessons')
            .where('status', 'in', ['CONFIRMED', 'INSTANT_CONFIRMED'])
            .get();

        const batch = db.batch();
        let updateCount = 0;

        for (const doc of snapshot.docs) {
            const lesson = doc.data();

            // Skip if timeSlot is missing or malformed
            if (!lesson.timeSlot || !lesson.timeSlot.includes('T')) {
                logger.warn(`Skipping lesson ${doc.id} due to invalid timeSlot format: ${lesson.timeSlot}`);
                continue;
            }

            // Handle instant lessons differently
            if (lesson.timeSlot.endsWith('instant')) {
                logger.info(`Processing instant lesson ${doc.id}`);
                batch.update(doc.ref, { status: 'COMPLETED' });
                updateCount++;
                continue;
            }

            try {
                // Parse the timeSlot
                const [datePart, timePart] = lesson.timeSlot.split('T');
                const [day, month, year] = datePart.split('/');
                const [hour, minute] = timePart.split(':');

                if (!year || !month || !day || !hour || !minute) {
                    logger.error(`Invalid time format for lesson ${doc.id}: ${lesson.timeSlot}`);
                    continue;
                }

                // Create dates in local timezone
                const lessonStartTime = new Date(
                    parseInt(year),
                    parseInt(month) - 1,
                    parseInt(day),
                    parseInt(hour) - 1,
                    parseInt(minute)
                );

                // Add one hour for lesson duration
                const lessonEndTime = new Date(lessonStartTime.getTime() + 60 * 60 * 1000);

                logger.info(`Processing lesson ${doc.id}:
                    Original time: ${lesson.timeSlot}
                    Lesson start: ${lessonStartTime.toISOString()}
                    Lesson end: ${lessonEndTime.toISOString()}
                    Current time: ${now.toISOString()}`);

                // Check if lesson has ended
                if (now > lessonEndTime) {
                    batch.update(doc.ref, { status: 'PENDING_REVIEW' });
                    updateCount++;
                    logger.info(`Marking lesson ${doc.id} as completed`);
                }
            } catch (parseError) {
                logger.error(`Error processing lesson ${doc.id}:`, parseError);
                continue;
            }
        }

        // Commit batch if there are updates
        if (updateCount > 0) {
            await batch.commit();
            logger.info(`Updated ${updateCount} lessons to completed status`);
        } else {
            logger.info('No lessons needed updating');
        }

        return null;
    } catch (error) {
        logger.error('Error checking lessons for completion:', error);
        throw error;
    }
});

//Check if lessons are reviewed and update their status accordingly
exports.checkReviewedLessons = onSchedule("*/30 * * * *", async (event) => {
    try {
        const now = new Date();
        const db = getFirestore();

        const snapshot = await db.collection('lessons')
            .where('status', '==', 'PENDING_REVIEW')
            .get();

        const batch = db.batch();
        let updateCount = 0;

        for (const doc of snapshot.docs) {
            const lesson = doc.data();
            if (!lesson.timeSlot) continue;

            const [datePart, timePart] = lesson.timeSlot.split('T');
            const [day, month, year] = datePart.split('/');
            const [hour, minute] = timePart.split(':');

            const lessonStartTime = new Date(Date.UTC(
                parseInt(year),
                parseInt(month) - 1,
                parseInt(day),
                parseInt(hour) - 1,
                parseInt(minute)
            ));

            const reviewEndTime = new Date(lessonStartTime.getTime() + (8 * 24 * 60 * 60 * 1000));

            logger.info(`Processing review for lesson ${doc.id}:
                Original time: ${lesson.timeSlot}
                Lesson start (UTC): ${lessonStartTime.toISOString()}
                Review end (UTC): ${reviewEndTime.toISOString()}
                Current time (UTC): ${now.toISOString()}`);

            if (now > reviewEndTime) {
                batch.update(doc.ref, { status: 'COMPLETED' });
                updateCount++;
                logger.info(`Marking lesson ${doc.id} as completed after review period`);
            }
        }

        if (updateCount > 0) {
            await batch.commit();
            logger.info(`Updated ${updateCount} lessons to completed status after review period`);
        }

        return null;
    } catch (error) {
        logger.error('Error checking reviewed lessons:', error);
        throw error;
    }
});