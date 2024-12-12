const admin = require("firebase-admin");

module.exports = {
  sendNotification: async (payload) => {
    try {
      await admin.messaging().send(payload);
      console.log(`Notification sent successfully to ${payload.token}`);
    } catch (error) {
      console.error(`Error sending notification:`, error);
    }
  },

  preparePayload: (title, body, token) => ({
    notification: {
      title,
      body,
    },
    token,
  }),
};
