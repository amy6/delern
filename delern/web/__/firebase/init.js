if (typeof firebase === 'undefined') throw new Error('hosting/init-error: Firebase SDK not detected. You must include it before /__/firebase/init.js');
firebase.initializeApp({
  "apiKey": "AIzaSyDSlTztVMTqFxhMhsR_9iEbEpoznPRLeeA",
  "databaseURL": "https://delern-debug.firebaseio.com",
  "storageBucket": "delern-debug.appspot.com",
  "authDomain": "delern-debug.firebaseapp.com",
  "messagingSenderId": "90374592783",
  "projectId": "delern-debug"
});
