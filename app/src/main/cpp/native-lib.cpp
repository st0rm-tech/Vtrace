#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_org_v_trace_MainActivity_getBaseUrl(JNIEnv* env, jobject /* this */) {
    // API URL'sini burada saklıyoruz
    std::string baseUrl = "https://stormapi.wineclo.com/";
    return env->NewStringUTF(baseUrl.c_str());
}
