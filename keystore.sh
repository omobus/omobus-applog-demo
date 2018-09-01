#!/bin/sh

DATE_MARK=`date +%Y%m%d%H%M`

keytool -genkey -v -keystore ./omobus.keystore_$DATE_MARK -alias omobus -keyalg RSA -keysize 2048 -validity 10800 -dname "CN=demo,OU=omobus,C=com"
