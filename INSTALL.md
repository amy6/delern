# Continuous integration and deploy setup

## Service Account and permissions

Accessing Developer API requires a special Google Cloud project, see
[documentation](https://developers.google.com/android-publisher/getting_started)
for how to set it up. The project is called `Google Play Android Developer`.
Once created, any service account on GCP console can be used to access API. The
account ID will look like `12345-compute@developer.googleserviceaccount.com`.

This account has to have access to:

* **Play Developer Console API**

  Permissions (set via Settings in Play Console):

  - Visibility
  - Edit store listing
  - Manage Alpha & Beta APKs
  - Manage Alpha & Beta users

  Enable API (to deploy App Engine applications via this project's account):

  - [Google Service Management API](https://console.developers.google.com/apis/api/servicemanagement.googleapis.com/)
  - [Google App Engine Admin API](https://console.developers.google.com/apis/api/appengine.googleapis.com/)

* **Android Keystore** (in GCS bucket)

  Storing data in a bucket requires billing account. Attaching billing account
  to a Firebase project will automatically switch it into paid plan. Therefore,
  we attach billing to Play Developer Console API project, and benefit from its
  ["always free"](https://cloud.google.com/free/docs/always-free-usage-limits)
  tier to store small amounts of data.

  The bucket is `gs://Google Play Android Developer/dasfoo-keystore`.
  No access to the bucket itself, but `Reader` permission to specific files:

  - `debug.keystore`, used to access Debug project from Firebase Test Lab
  - `delern.jks`, **only** used to sign the release version

  Both files can be generated with a command similar to:

  ```shell
  $ keytool -genkey -v -keystore debug.keystore -alias androiddebugkey \
      -storepass android -keypass android -keyalg RSA -validity 14000 \
      -dname 'CN=debug.dasfoo.org, OU=Debug, O=DasFoo, L=Zurich, S=ZH, C=CH'
  ```

* **Firebase Test Results** (in GCS bucket)

  For the reasons above, this storage bucket also has to belong to a
  non-Firebase project, so it's stored in
  `gs://Google Play Android Developer/delern-test-results`. To prevent the
  bucket from growing above "always free" tier quota, a lifecycle policy is set
  on it (e.g. 95 days).

  The account has `Owner` access to the bucket to be able to upload test
  results.

* **Firebase Test Lab**

  `Editor` access to the Google Cloud project for debug purposes
  (`delern-debug`) to run tests in Firebase Test Lab.

* **AppEngine deployment**

  `Editor` access to Google Cloud project for release (`delern-e1b33`) to be
  able to deploy cronjobs and change traffic split to the newly deployed
  version.

## Firebase settings and google-services.json

Firebase project settings are located under a small gear in the side panel.
You have to add "debug" and "instrumented" apps to the Debug project
(`delern-debug`), and production app into the Release project. The Release
project must only contain a single SHA-1, the one from the release keystore. The
SHA-1 can be obtained with

```shell
$ keytool -exportcert -keystore <keystore path> -list -v
```

The Debug project "debug" app must contain SHA-1 of every developer's
`$HOME/.android/debug.keystore` key aliased as `androiddebugkey`. The Debug
project "instrumented" app must contain the same plus SHA-1 of the key in
`debug.keystore` used by CI. The password for the debug keystore is `android`.

After all the SHA-1 are set, you can download `google-services.json` and put the
one from Debug project into `app`, and the one from Release into
`app/src/release`.

## Crashlytics

Put `fabric.properties` with Fabric API key configured into `app` subdirectory.
You can get the API key (not secret key, which isn't used) on the Fabric
[organization page](https://www.fabric.io/settings/organizations).

## CircleCI environment

* `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEYSTORE_KEY_PASSWORD`

  Passwords to access release version of Android keystore.

* `FIREBASE_TOKEN`

  Obtained via `firebase login:ci` for release (production, non-debug) Firebase
  project. Used by `firebase deploy` to publish Database Security Rules,
  website, Cloud Functions. More information on the GitHub page of
  [Firebase](https://github.com/firebase/firebase-tools#using-with-ci-systems).

* `GCLOUD_SERVICE_KEY`

  Base64-encoded private key for a Google Cloud Service Account in `JSON`
  format, which can be obtained from Google Cloud Console in IAM section.

* `DEBUG_FIREBASE_SERVICE_KEY`

  Base64-encoded private key for a Firebase Admin Service Account in `JSON`
  format, which can be obtained from Firebase Console in Project Settings
  "Service Accounts" tab section for "debug" project.

* `VERSIONEYE_API_KEY`

  API key for https://www.versioneye.com/, a system for reporting dependency
  versions and notifying about important updates.
