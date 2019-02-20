#!/usr/bin/env bash

# These files are neccessary to build debug version of the app. There are no
# real secrets inside.

cd "$(dirname "$0")"

cp ci/google-services.json \
   ../flutter/android/app/google-services.json
cp ci/org.dasfoo.delern.debug \
   ../flutter/ios/Runner/GoogleService-Info/org.dasfoo.delern.debug

mkdir -p "${HOME?}/.android"
cp ci/debug.keystore "${HOME?}/.android/debug.keystore"
