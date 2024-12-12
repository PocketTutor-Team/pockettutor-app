const { getFirestore } = require("firebase-admin/firestore");
const { sendNotification, preparePayload } = require("./notification");


const db = getFirestore();

module.exports = {
    // Notify student when the lesson is waiting for their review
  notifyStudentLessonPendingReview: async (lesson) => {
    const studentUid = lesson.studentUid;

    try {
      // Fetch the student profile
      const studentDoc = await db.collection("profiles").where("uid", "==", studentUid).limit(1).get();

      if (studentDoc.empty) {
        console.error("Student profile not found.");
        return;
      }

      const studentData = studentDoc.docs[0].data();
      const studentPayload = preparePayload(
        "Lesson Pending Review!",
        `Your lesson with the tutor is now complete and is waiting for your review.`,
        studentData.token
      );

      // Send notification
      await sendNotification(studentPayload);
    } catch (error) {
      console.error("Error in notifyStudentLessonPendingReview:", error);
    }
  },

    // Notify the student whenever an additional tutor offers to teach a lesson he requested
  notifyStudentAsTutorOfferedToTeach: async (lesson) => {
    const studentUid = lesson.studentUid;

    try {
      // Fetch student profile
      const studentDoc = await db.collection("profiles").where("uid", "==", studentUid).limit(1).get();

      if (studentDoc.empty) {
        console.error("Student profile not found.");
        return;
      }

      const studentData = studentDoc.docs[0].data();
      const studentPayload = preparePayload(
        "Tutor Offered to Teach!",
        `A tutor has offered to teach your lesson "${lesson.title}". Please review and confirm!`,
        studentData.token
      );

      // Send notification
      await sendNotification(studentPayload);
    } catch (error) {
      console.error("Error in notifyStudentTutorOffered:", error);
    }
  },

    // Notify the tutor when a student confirms a lesson he offered to teach
  notifyStudentLessonConfirmedByTutor: async (lesson) => {
    // retrieve the first tutor UID from the lesson
    const tutorUid = String(lesson.tutorUid).split(",")[0];
    // retrieve the unique student UID from the lesson
    const studentUid = lesson.studentUid;

    try {
        // retrieve the tutor and student profile documents with a Promise to allow for parallel execution
      const [tutorDoc, studentDoc] = await Promise.all([
        db.collection("profiles").where("uid", "==", tutorUid).limit(1).get(),
        db.collection("profiles").where("uid", "==", studentUid).limit(1).get(),
      ]);

      if (tutorDoc.empty || studentDoc.empty) {
        console.error("Tutor or Student profile not found.");
        return;
      }

      const tutorData = tutorDoc.docs[0].data();
      const studentData = studentDoc.docs[0].data();

      const studentPayload = preparePayload(
        "Lesson Confirmed!",
        `Your lesson with ${tutorData.firstName} has been confirmed. See you on ${lesson.timeSlot}!`,
        studentData.token
      );

      // Send notifications
      await sendNotification(studentPayload);
    } catch (error) {
      console.error("Error in notifyStudentLessonConfirmedByTutor:", error);
    }
  },

  notifyStudentInstantLessonConfirmed: async (lesson) => {
        const student = String(lesson.studentUid).split(",")[0];

        try {
          // Fetch the tutor's profile
          const studentQuery = await db.collection("profiles").where("uid", "==", studentUid).limit(1).get();

          if (studentQuery.empty) {
            console.error("Tutor profile not found.");
            return;
          }

          const studentData = studentQuery.docs[0].data();

          // Prepare the notification payload for the tutor
          const studentPayload = preparePayload(
            "Instant Lesson Confirmed!",
            `Great news, a tutor is available to help you now!`,
            studentData.token
          );

          // Send the notification
          await sendNotification(studentPayload);
          console.log("Notification sent to student for lesson confirmation.");
        } catch (error) {
          console.error("Error in notifyStudentInstantLessonConfirmed:", error);
        }
      },

    notifyTutorCancelled: async (lesson) => {
      const studentUid = String(lesson.studentUid).split(",")[0];

      try {
        const studentDoc = await db.collection("profiles").where("uid", "==", studentUid).limit(1).get();

         if (studentDoc.empty) {
            console.error("Tutor profile not found.");
            return;
         }

         const studentData = studentDoc.docs[0].data();
         const studentPayload = preparePayload(
            "Your lesson as been cancelled.",
            `A tutor has cancelled his lesson with you.`,
            tutorData.token
         );

         // Send notification
         await sendNotification(tutorPayload);
      } catch (error) {
        console.error("Error in notifyStudentCancelled:", error);
      }

    },

    // Notify the tutor when a student cancels a lesson he offered to teach
    notifyStudentCancelled: async (lesson) => {
       const tutorUid = String(lesson.tutorUid).split(",")[0];

       try {
         const tutorDoc = await db.collection("profiles").where("uid", "==", tutorUid).limit(1).get();

          if (tutorDoc.empty) {
            console.error("Tutor profile not found.");
            return;
          }

          const tutorData = tutorDoc.docs[0].data();
          const tutorPayload = preparePayload(
            "Your lesson as been cancelled.",
            `A student has cancelled his lesson with you.`,
            tutorData.token
          );

          // Send notification
          await sendNotification(tutorPayload);
       } catch (error) {
         console.error("Error in notifyStudentCancelled:", error);
       }

    },
    // Notify the tutor when a student confirms a lesson he offered spontanously to teach
    notifyTutorForConfirmation: async (lesson) => {
      const tutorUid = String(lesson.tutorUid).split(",")[0];

      try {
        const tutorDoc = await db.collection("profiles").where("uid", "==", tutorUid).limit(1).get();

        if (tutorDoc.empty) {
          console.error("Tutor profile not found.");
          return;
        }

        const tutorData = tutorDoc.docs[0].data();
        const tutorPayload = preparePayload(
          "New Lesson Request",
          `A student has requested a lesson. Please confirm or decline.`,
          tutorData.token
        );

        // Send notification
        await sendNotification(tutorPayload);
      } catch (error) {
        console.error("Error in notifyTutorForConfirmation:", error);
      }
    },

  // Notify the tutor when a student confirms the lesson that the student initially requested
  notifyTutorLessonConfirmedByStudent: async (lesson) => {
      const tutorUid = String(lesson.tutorUid).split(",")[0];

      try {
        // Fetch the tutor's profile
        const tutorQuery = await db.collection("profiles").where("uid", "==", tutorUid).limit(1).get();

        if (tutorQuery.empty) {
          console.error("Tutor profile not found.");
          return;
        }

        const tutorData = tutorQuery.docs[0].data();

        // Prepare the notification payload for the tutor
        const tutorPayload = preparePayload(
          "Lesson Confirmed!",
          `Great news! The student has accepted your tutoring offer for ${lesson.title}.`,
          tutorData.token
        );

        // Send the notification
        await sendNotification(tutorPayload);
        console.log("Notification sent to tutor for lesson confirmation.");
      } catch (error) {
        console.error("Error in notifyTutorLessonConfirmedByStudent:", error);
      }
    },


    // Notify the tutor when a student confirms a lesson he offered spontanously to teach
  notifyTutorForConfirmation: async (lesson) => {
    const tutorUid = String(lesson.tutorUid).split(",")[0];

    try {
      const tutorDoc = await db.collection("profiles").where("uid", "==", tutorUid).limit(1).get();

      if (tutorDoc.empty) {
        console.error("Tutor profile not found.");
        return;
      }

      const tutorData = tutorDoc.docs[0].data();
      const tutorPayload = preparePayload(
        "New Lesson Request",
        `A student has requested a lesson. Please confirm or decline.`,
        tutorData.token
      );

      // Send notification
      await sendNotification(tutorPayload);
    } catch (error) {
      console.error("Error in notifyTutorForConfirmation:", error);
    }
  },
};
