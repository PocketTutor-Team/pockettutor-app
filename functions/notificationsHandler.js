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
  } else {
    console.log(`No status change detected.`);
  }
});
