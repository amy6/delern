#!/usr/bin/env bash

set -e

if [ -z "$CI" -a -t 1 ]; then
	echo 'This script is not supposed to be run outside of CI. It may'
	echo 'silently overwrite important files on your filesystem. Press'
	echo 'Ctrl-C to stop now, or ENTER to continue at your own risk.'
	read
	echo 'Proceeding...'
fi

set -x

cd "$(dirname "$0")"

# These files are neccessary to build debug version of the app. There are no
# real secrets inside.

cp ci/google-services.json \
   ../flutter/android/app/google-services.json
cp ci/org.dasfoo.delern.debug \
   ../flutter/ios/Runner/GoogleService-Info/org.dasfoo.delern.debug

mkdir -p "${HOME?}/.android"
cp ci/debug.keystore "${HOME?}/.android/debug.keystore"
