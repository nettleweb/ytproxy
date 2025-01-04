#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <locale.h>
#include <unistd.h>

void throwErrno(JNIEnv* env, int code, const char* cmd) {
	if (code != 0) {
		jclass except = (*env)->FindClass(env, "com/nettleweb/client/VM$ErrnoException");
		jmethodID init = (*env)->GetMethodID(env, except, "<init>", "(ILjava/lang/String;)V");
		(*env)->Throw(env, (*env)->NewObject(env, except, init, (jint) code, (*env)->NewStringUTF(env, cmd)));
	}
}

void Java_com_nettleweb_client_VM_exit(JNIEnv* env, jclass cls, jint code) {
	exit(code);
}

void Java_com_nettleweb_client_VM_nice(JNIEnv* env, jclass cls, jint code) {
	throwErrno(env, nice((int) code), "nice");
}

void Java_com_nettleweb_client_VM_fsync(JNIEnv* env, jclass cls, jint fd) {
	throwErrno(env, fsync((int) fd), "fsync");
}

void Java_com_nettleweb_client_VM_close(JNIEnv* env, jclass cls, jint fd) {
	throwErrno(env, close((int) fd), "close");
}

void Java_com_nettleweb_client_VM_abort(JNIEnv* env, jclass cls) {
	abort();
}

void Java_com_nettleweb_client_VM_debug(JNIEnv* env, jclass cls) {
	sync();
	nice(10);
	setenv("LANG", "C", 1);
	setenv("LC_ALL", "C", 1);
	setlocale(LC_ALL, "C.UTF-8");
}

jint Java_com_nettleweb_client_VM_system(JNIEnv* env, jclass cls, jstring cmd) {
	const char* ccmd = (*env)->GetStringUTFChars(env, cmd, 0);
	jint code = (jint) system(ccmd);
	(*env)->ReleaseStringUTFChars(env, cmd, ccmd);
	return code;
}

jlong Java_com_nettleweb_client_VM_00024Memory_objectPtr(JNIEnv *env, jclass cls, jobject obj) {
	return (jlong) (void *) obj;
}

jbyteArray Java_com_nettleweb_client_VM_00024Memory_getRaw(JNIEnv *env, jclass cls, jlong ptr, jint len) {
	jbyteArray arr = (*env)->NewByteArray(env, len);
	(*env)->SetByteArrayRegion(env, arr, 0, len, (void*) ptr);
	return arr;
}

void Java_com_nettleweb_client_VM_00024Memory_setRaw(JNIEnv *env, jclass cls, jlong ptr, jbyteArray buf) {
	jint length = (*env)->GetArrayLength(env, buf);
	jbyte* data = (*env)->GetByteArrayElements(env, buf, 0);
	memcpy((void *) ptr, (void *) data, (size_t) length);
}

jlong Java_com_nettleweb_client_VM_00024Memory_malloc(JNIEnv *env, jclass cls, jlong size) {
	return (jlong) malloc((size_t) size);
}

void Java_com_nettleweb_client_VM_00024Memory_free(JNIEnv *env, jclass cls, jlong ptr) {
	free((void *) ptr);
}

jclass Java_com_nettleweb_client_VM_00024Reflect_getClass(JNIEnv *env, jclass cls, jstring name) {
	const char* cname = (*env)->GetStringUTFChars(env, name, 0);
	cls = (*env)->FindClass(env, cname);
	(*env)->ReleaseStringUTFChars(env, name, cname);
	return cls;
}

jobject Java_com_nettleweb_client_VM_00024Reflect_newObject(JNIEnv *env, jclass cls, jclass obj) {
	return (*env)->AllocObject(env, obj);
}

void Java_com_nettleweb_client_VM_00024Reflect_setAccessible(JNIEnv *env, jclass cls, jobject obj, jboolean flag) {
	(*env)->SetBooleanField(env, obj, (*env)->GetFieldID(env, (*env)->GetObjectClass(env, obj), "override", "Z"), flag);
}

jint Java_com_nettleweb_client_VM_00024Stdin_read(JNIEnv *env, jobject obj, jbyteArray buf, jint off, jint len) {
	void* data = malloc((size_t) len);
	jint i = (jint) read(0, data, (size_t) len);
	(*env)->SetByteArrayRegion(env, buf, off, len, (const jbyte*) data);
	free(data);
	return i;
}

void Java_com_nettleweb_client_VM_00024Stdout_write(JNIEnv *env, jobject obj, jbyteArray buf, jint off, jint len) {
	void* data = malloc((size_t) len);
	(*env)->GetByteArrayRegion(env, buf, off, len, data);
	write(1, data, (size_t) len);
	free(data);
}

void Java_com_nettleweb_client_VM_00024Stderr_write(JNIEnv *env, jobject obj, jbyteArray buf, jint off, jint len) {
	void* data = malloc((size_t) len);
	(*env)->GetByteArrayRegion(env, buf, off, len, data);
	write(2, data, (size_t) len);
	free(data);
}

int main(const int argc, const char** argv) {
	return 0;
}
