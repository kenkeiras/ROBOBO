/*
 * This file is part of the AprilTag library.
 *
 * AprilTag is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * AprilTag is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Libav; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 */
#ifndef _TIME_PROFILE_H
#define _TIME_PROFILE_H

#include <stdio.h>
#include <sys/time.h>
#include <string.h>
#include <stdint.h>
#include "zarray.h"

struct timeprofile_entry
{
    char name[32];
    int64_t utime;
};

typedef struct timeprofile timeprofile_t;
struct timeprofile
{
    int64_t utime;
    zarray_t *stamps;
};

static inline int64_t utime_now()
{
    struct timeval tv;
    gettimeofday (&tv, NULL);
    return (int64_t) tv.tv_sec * 1000000 + tv.tv_usec;
}

static inline timeprofile_t *timeprofile_create()
{
    timeprofile_t *tp = calloc(1, sizeof(timeprofile_t));
    tp->stamps = zarray_create(sizeof(struct timeprofile_entry));

    tp->utime = utime_now();

    return tp;
}

static inline void timeprofile_destroy(timeprofile_t *tp)
{
    zarray_destroy(tp->stamps);
    free(tp);
}

static inline void timeprofile_clear(timeprofile_t *tp)
{
    zarray_clear(tp->stamps);
    tp->utime = utime_now();
}

static inline void timeprofile_stamp(timeprofile_t *tp, const char *name)
{
    struct timeprofile_entry tpe;

    strncpy(tpe.name, name, sizeof(tpe.name));
    tpe.name[sizeof(tpe.name)-1] = 0;
    tpe.utime = utime_now();

    zarray_add(tp->stamps, &tpe);
}

static inline void timeprofile_display(timeprofile_t *tp)
{
    int64_t lastutime = tp->utime;

    for (int i = 0; i < zarray_size(tp->stamps); i++) {
        struct timeprofile_entry *stamp;

        zarray_get_volatile(tp->stamps, i, &stamp);

        double cumtime = (stamp->utime - tp->utime)/1000000.0;

        double parttime = (stamp->utime - lastutime)/1000000.0;

        printf("%2d %32s %15f ms %15f ms\n", i, stamp->name, parttime*1000, cumtime*1000);

        lastutime = stamp->utime;
    }
}

static inline uint64_t timeprofile_total_utime(timeprofile_t *tp)
{
    if (zarray_size(tp->stamps) == 0)
        return 0;

    struct timeprofile_entry *first, *last;
    zarray_get_volatile(tp->stamps, 0, &first);
    zarray_get_volatile(tp->stamps, zarray_size(tp->stamps) - 1, &last);

    return last->utime - first->utime;
}

#endif
