#!/usr/bin/env bash
# Hacky workaround
# @TODO replace it with an apropiate gradle configuration (when updated)

CONCURRENCY=8

if [ -z "$ANDROID_NDK_PATH" ];then
    ANDROID_NDK_PATH=~/android-ndk/
fi

set -euo pipefail
IFS=$'\n\t'

export ANDROID_NDK="$ANDROID_NDK_PATH"
export ANDROID_NATIVE_API_LEVEL=android-19

if [ ! -d jni/opencv/ ];then
    echo "Downloading OpenCV..."

    git clone git://code.opencv.org/opencv.git jni/opencv/
fi


if [ ! -f jni/opencv/platforms/build_android_arm/OpenCV.mk ];then
    echo "Building OpenCV..."
    cd jni/opencv/platforms
    sh scripts/cmake_android_arm.sh
    cd build_android_arm
    make -j$CONCURRENCY

    cd ../../../../
fi

echo "Building NDK modules..."
$ANDROID_NDK_PATH/ndk-build $@ || exit 1

echo "Packaging modules into .jar files"
# Copy OpenCV headers
for f in jni/opencv/platforms/build_android_arm/lib/*/*.so;do
    o=`echo "$f"|sed 's/jni\/opencv\/platforms\/build_android_arm\/lib/libs/g'`
    cp -v "$f" "$o"
done

for f in `find libs -name "*.so"`;do
    n=`basename $f|sed -r 's/(.*)\.[^.]*$/\1/'`
    d=`dirname $f`
    p=`dirname $f|sed 's/^libs/lib/'`

    # Had to pack it into jars, but have it's path start with 'lib'
    # instead of 'libs' so... python!
    python -c "from zipfile import *; ZipFile('$d/$n.jar', 'w').write('$f', '$p/$n.so')"
done
