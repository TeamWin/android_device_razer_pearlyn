#!/bin/bash

VENDOR=razer
DEVICE=pearlyn

BASE=../../../vendor/$VENDOR/$DEVICE/proprietary
#rm -rf $BASE/*

for FILE in `cat proprietary-files.txt | grep -v ^# | grep -v ^$ `; do
    DIR=`dirname $FILE`
    if [ ! -d ./VEN/$DIR ]; then
        mkdir -p ./VEN/$DIR
    fi
    #adb pull /system/$FILE $BASE/$FILE
    cp -rf ~/cm13_pearlyn/vendor/razer/pearlyn/proprietary/$FILE ./VEN/$FILE
done

./setup-makefiles.sh
