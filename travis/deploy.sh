#!/usr/bin/env bash

# NOTE: bintray doesn't like hosting CI artifacts
# NOTE: if we're going to put these builds somewhere the JFrog Artifactory might be an option
# NOTE: but an account is required, so handling deployments is on hold for the moment

set -e

project="$(basename $(pwd))"
webFileName="${project}-web.zip"
desktopFileName="$(ls -1 desktop/build/libs/desktop-*.jar | tail -n 1 | perl -pe 'chomp')"

if [[ -n $TRAVIS_BUILD_NUMBER ]]; then
  bintrayVersion="build-$TRAVIS_BUILD_NUMBER"
else
  bintrayVersion="$(./gradlew printVersion | perl -ne 'chomp; print if $prevline eq ":printVersion"; $prevline = $_;')"
fi

bintrayApiUrl="https://api.bintray.com/content/${BINTRAY_USER}/${BINTRAY_REPO}/${project}"

for e in BINTRAY_USER BINTRAY_API_KEY BINTRAY_REPO project bintrayVersion webFileName bintrayApiUrl; do
  if [[ -z $(eval "echo \$$e") ]]; then
    echo "$e is a required environment variable"
    exit 1
  fi
done

if [[ -d deploy ]]; then
  rm -r deploy
fi
if [[ -e $webFileName ]]; then
  rm "$webFileName"
fi

mkdir deploy
cp -r html/build/dist/* deploy/

cd deploy
zip -r "../${webFileName}" ./
cd ..

# Upload to bintray
printf "\nUploading desktop to bintray:\n"
curl -H "X-Bintray-Publish: 1" -H "X-Bintray-Override: 1" -T "${desktopFileName}" -u"${BINTRAY_USER}:${BINTRAY_API_KEY}" "${bintrayApiUrl}/${bintrayVersion}/${project}-${bintrayVersion}-desktop.jar"
printf "\nUploading web to bintray:\n"
curl -H "X-Bintray-Publish: 1" -H "X-Bintray-Override: 1" -T "${webFileName}" -u"${BINTRAY_USER}:${BINTRAY_API_KEY}" "${bintrayApiUrl}/${bintrayVersion}/${project}-${bintrayVersion}-web.zip"

versionsToDelete="$(curl "${bintrayApiUrl}/files" | jq -r '. | unique_by(.version) | sort_by(.created) | .[0:-10] | .[].version')"

if [[ -n $versionsToDelete ]]; then
  for v in $versionsToDelete; do
    curl -u "${BINTRAY_USER}:${BINTRAY_API_KEY}" -X DELETE "${bintrayApiUrl}/versions/${v}"
  done
fi
