#!/usr/bin/env bash

# Try to guess Android NDK path
if [ -z "$ANDROID_NDK_PATH" ];then
    ANDROID_NDK_PATH=~/android-ndk/
fi

# Check whether or not is android NDK available
HAS_NDK="false"
if [ -d "$ANDROID_NDK_PATH" ];then
    HAS_NDK="true"
    export ANDROID_NDK_PATH
fi

# Fail hard
set -eu
set -o pipefail
IFS=$'\n\t'

# Prints something underlined
TITLE (){
    echo "$@"
    echo "$@"|sed 's/./-/g'
    echo ""
}

# Prints something in blue
INFO (){
    echo -en "INFO: \x1b[0;94m"
    echo -n  "$@"
    echo -e "\x1b[0m"
}


TITLE "Installing rosjava directory (udc_robot_control_java, msgs and control panel)"
cd rosjava/

# First built everything with catkin
catkin_make

# Then install the packages in the local maven repo
cd src/ROBOBO/
bash ./gradlew install

cd ../../../


TITLE "Building android core"
cd android_core/udc_robot_control_android/
bash ./gradlew build

if [ "$HAS_NDK" == "true" ];then
    TITLE "Building android core NDK"
    bash build-ndk.sh
    echo ""
fi

cd ../../

TITLE "Building prototipo"
cd prototipo/
bash ./gradlew build

cd ../../


# Give some instructions to build android sides
INFO $'To install android core in a device go to \x1b[1;94mandroid_core/udc_robot_control_android/\x1b[0;94m'
INFO $'and run \x1b[1;92mbash ./gradlew installDebug\x1b[0;94m'
INFO $'or, to install prototype go to \x1b[1;94mprototipo/\x1b[0;94m'
INFO $'and run \x1b[1;92mbash ./gradlew installUsbHostDebug\x1b[0;94m'
