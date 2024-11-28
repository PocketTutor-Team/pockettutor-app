const { onSchedule } = require("firebase-functions/v2/scheduler");
const { setGlobalOptions } = require("firebase-functions/v2");
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");
const logger = require("firebase-functions/logger");

initializeApp();

setGlobalOptions({
    maxInstances: 10,
    timeoutSeconds: 60,
    memory: "256MiB"
});

exports.checkCompletedLessons = onSchedule("* * * * *", async (event) => {
    try {
        const now = new Date();
        const db = getFirestore();

        const snapshot = await db.collection('lessons')
            .where('status', 'in', ['CONFIRMED', 'INSTANT_CONFIRMED'])
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
            const lessonEndTime = new Date(lessonStartTime.getTime() + 60 * 60 * 1000);

            logger.info(`Processing lesson ${doc.id}:
                Original time: ${lesson.timeSlot}
                Lesson start (UTC): ${lessonStartTime.toISOString()}
                Lesson end (UTC): ${lessonEndTime.toISOString()}
                Current time (UTC): ${now.toISOString()}`);

            if (now > lessonEndTime) {
                batch.update(doc.ref, { status: 'PENDING_REVIEW' });
                updateCount++;
                logger.info(`Marking lesson ${doc.id} as pending review`);
            }
        }

        if (updateCount > 0) {
            await batch.commit();
            logger.info(`Updated ${updateCount} lessons to pending review status`);
        }

        return null;
    } catch (error) {
        logger.error('Error checking lessons for review:', error);
        throw error;
    }
});

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