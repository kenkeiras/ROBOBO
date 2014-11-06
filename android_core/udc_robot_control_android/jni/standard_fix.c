#include <android/log.h>
#include <stdlib.h>
#include <time.h>

// Temporary workaround for
// https://code.google.com/p/android/issues/detail?id=58888

/**
 * Provides the stdlib random() function.
 *
 * It's temporary, so it's kept pretty simple.
 */
long random(){
    static int seeded = 0;
    static long state = 0;
    if (!seeded){
        state = time(NULL);
        seeded = 1;
        random(); // Run one iteration before
    }


    int step = state;

    // a = 1103515245, c = 12345
    state = (1103515245 * state) + 12345;

    return step;
}


// Not needed, not implemented
double atof(const char *nptr){
    __android_log_print(ANDROID_LOG_ERROR, "UDC_NDK", "Trying to atof(“%s”) but it's not implemented\n", nptr);
    abort();
    return 0;
}
