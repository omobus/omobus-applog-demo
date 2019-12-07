#!/bin/sh

vstamp='none'

for i in `grep propVersionName ./gradle.properties | tr '=' '\n'`
do
    vstamp=$i
done;

./gradlew clean
./gradlew assembleRelease
cp ./build/outputs/apk/release/omobus-applog-demo-release.apk ../omobus-applog-demo-$vstamp.apk
./gradlew clean

echo "omobus-applog-demo $vstamp has been compiled."
