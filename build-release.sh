#!/bin/sh

./gradlew clean && ./gradlew assembleRelease && cp ./build/outputs/apk/release/omobus-applog-demo-*.apk ../ && ./gradlew clean
