#!/usr/bin/env bash

# Set verbose and xtrace on to give the user a better idea of what is happening when the script runs.
set -xv

# Assume that we are using a dumb terminal (produces cleaner output on build servers).
export TERM="dumb"

# Nothing to install without an android module...
#export ANDROID_HOME="$(pwd)/travis/android-sdk-linux"
#
## Check if ANDROID_HOME contains android tools.
#if [ ! -x "${ANDROID_HOME}/tools/android" ]; then
#
#	# Set the version of the Android SDK to install.
#	android_sdk_version="24.0.1"
#
#	# Download the Android SDK.
#	wget --quiet "http://dl.google.com/android/android-sdk_r${android_sdk_version}-linux.tgz"
#
#	# Install the Android SDK to the travis folder.
#	tar -C travis -xzf "android-sdk_r${android_sdk_version}-linux.tgz"
#
#  # Add the Android SDK tools to the PATH.
#  export PATH="${PATH}:${ANDROID_HOME}/tools"
#
#  # Install/update the required Android SDK components.
#  echo yes | android update sdk -a --filter "platform-tools,build-tools-20.0.0,android-20" --no-ui --force > /dev/null
#fi
