#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

extern "C" JNICALL
Java_com_sharerideexpense_easycarpool_Signin_apiKey(JNIEnv *env, jobject object) {

   std::string api_key = "AIzaSyAfD5JKVG1Tkqqn7dHrC7QnIz-9QjJNBOY";

   return env->NewStringUTF(api_key.c_str());
}
