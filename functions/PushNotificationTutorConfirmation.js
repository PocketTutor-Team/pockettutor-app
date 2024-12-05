const {onDocumentUpdated} = require("firebase-functions/v2/firestore");
const {setGlobalOptions} = require("firebase-functions/v2");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const admin = require("firebase-admin");

initializeApp();

setGlobalOptions({
  maxInstances: 10,
  timeoutSeconds: 60,
  memory: "256MiB"});

exports.pushNotificationTutorLessonConfirmation = onDocumentUpdated(
    "lessons/{lessonId}",
    async (event) => {
      const before = event.data.before.data(); // Data before the update
      const after = event.data.after.data(); // Data after the update
      const lessonId = event.params.lessonId;
      const db = getFirestore();

      // Check if the status field changed
      if (before.status !== after.status) {
        console.log(`Lesson ${lessonId} status changed from ${before.status}
           to ${after.status}`);

        // Check if the new status is "confirmed"
        if (before.status === "PENDING_TUTOR_CONFIRMATION" && after.status ===
           "CONFIRMED") {
          try {
            const tutorUid = String(after.tutorUid).split(",")[0];
            const studentUid = after.studentUid;
            console.log(`idcollected, ${tutorUid} && ${studentUid}`);
            const tutorQuery = await db.collection("profiles")
                .where("uid", "==", tutorUid).limit(1).get();
            console.log(`tutorDoc`);
            const studentQuery = await db.collection("profiles")
                .where("uid", "==", studentUid).limit(1).get();
            console.log(`studentDoc`);

            if (tutorQuery.empty || studentQuery.empty) {
              console.error("Tutor or Student profile not found.");
              return;
            }

            const tutorDoc = tutorQuery.docs[0];
            const studentDoc = studentQuery.docs[0];

            if (!tutorDoc.exists || !studentDoc.exists) {
              console.error("Tutor or Student profile not found.");
              return;
            }

            const tutorToken = tutorDoc.data().token;
            const studentToken = studentDoc.data().token;
            const tutorName = tutorDoc.data().firstName;
            const studentName = studentDoc.data().firstName;

            console.log(`Tutor Token: ${tutorToken}`);
            console.log(`Student Token: ${studentToken}`);

            if (tutorToken === "" || studentToken === "" ||
                tutorName === "" || studentName === "") {
              console.error(`Missing required fields in profiles.
                Notification not sent.`);
              return;
            }

            const payloadStudent = {
              notification: {
                title: "Lesson Confirmed!",
                body: `Your lesson with ${tutorName} has been confirmed.
                See you on ${after.timeSlot}!`,
              },
              token: studentToken,
            };

            const payloadTutor = {
              notification: {
                title: "Lesson Confirmed!",
                body: `Your lesson with ${studentName} has been confirmed.
                See you on ${after.timeSlot}!`,
              },
              token: tutorToken,
            };

            try {
              await admin.messaging().send(payloadStudent);
            } catch (error) {
              console.error(`Error sending notification to student:`, error);
            }

            try {
              await admin.messaging().send(payloadTutor);
            } catch (error) {
              console.error(`Error sending notification to tutor:`, error);
            }

            console.log(`Notifications sent for lesson ${lessonId}`);
          } catch (error) {
            console.error(`Error sending notifications for lesson
              ${lessonId}:`, error);
          }
        } else {
          console.log(`No notification sent. Current status: ${after.status}`);
        }
      } else {
        console.log(`Lesson ${lessonId}: No change in status.`);
      }
    },
);
