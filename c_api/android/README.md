# C2PA Android 

This file provides information about building the c2pa library using the c_api exposed as a Java Native Interface

# How to build

1. Install the android-ndk version 26b into your environment
2. run "> make release-android" to build the native libraries for Android arm64 and x86_64
3. Add the generated libraries to your jniLibs folder in your Android project
4. Add the c2pa.kt (see below) into your project source code under the org.c2pa package path
5. Do amazing things with c2pa on Android! 

# c2pa.kt JNI wrapper

This folder contains the c2pa.kt Kotlin Android wrapper class for calling into the native functions in the c_api built library via Java Native Interface calls. The class are defined in the org.c2pa package and must be put into that structure in any Android project it is added to. 

