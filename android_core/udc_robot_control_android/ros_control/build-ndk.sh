#!/usr/bin/env bash
# Hacky workaround
# @TODO replace it with an apropiate gradle configuration (when updated)

if [ -z "$ANDROID_NDK_PATH" ];then
    ANDROID_NDK_PATH=~/android-ndk/
fi

$ANDROID_NDK_PATH/ndk-build || exit 1
for f in `find libs -name "*.so"`;do
    n=`basename $f|cut -d. -f1`
    d=`dirname $f`
    p=`dirname $f|sed 's/^libs/lib/'`

    # Had to pack it into jars, but have it's path start with 'lib'
    # instead of 'libs' so... python!
    python -c "from zipfile import *; ZipFile('$d/$n.jar', 'w').write('$f', '$p/$n.so')"
done
