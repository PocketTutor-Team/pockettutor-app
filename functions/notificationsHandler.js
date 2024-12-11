const { onDocumentUpdated } = require("firebase-functions/v2/firestore");
const { initializeApp } = require("firebase-admin/app");
const { notifyLessonConfirmed, notifyLessonPending } = require("./lessonNotifications");

initializeApp();


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

