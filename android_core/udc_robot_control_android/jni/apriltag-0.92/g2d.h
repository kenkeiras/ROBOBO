#ifndef _G2D_H
#define _G2D_H

#include "zarray.h"

// This library tries to avoid needless proliferation of types.
//
// A point is a double[2]. (Note that when passing a double[2] as an
// argument, it is passed by pointer, not by value.)
//
// A polygon is a zarray_t of double[2]. (Note that in this case, the
// zarray contains the actual vertex data, and not merely a pointer to
// some other data. IMPORTANT: A polygon must be specified in CCW
// order.  It is implicitly closed (do not list the same point at the
// beginning at the end.
//
// Where sensible, it is assumed that objects should be allocated
// sparingly; consequently "init" style methods, rather than "create"
// methods are used.

////////////////////////////////////////////////////////////////////
// Lines

typedef struct
{
    // Internal representation: a point that the line goes through (p) and
    // the direction of the line (u).
    double p[2];
    double u[2]; // always a unit vector
} g2d_line_t;

// initialize a line object.
void g2d_line_init_from_points(g2d_line_t *line, const double p0[2], const double p1[2]);

// The line defines a one-dimensional coordinate system whose origin
// is p. Where is q? (If q is not on the line, the point nearest q is
// returned.
double g2d_line_get_coordinate(const g2d_line_t *line, const double q[2]);

// Intersect two lines. The intersection, if it exists, is written to
// p (if not NULL), and 1 is returned. Else, zero is returned.
int g2d_line_intersect_line(const g2d_line_t *linea, const g2d_line_t *lineb, double *p);

////////////////////////////////////////////////////////////////////
// Line Segments. line.p is always one endpoint; p1 is the other
// endpoint.
typedef struct
{
    g2d_line_t line;
    double p1[2];
} g2d_line_segment_t;

void g2d_line_segment_init_from_points(g2d_line_segment_t *seg, const double p0[2], const double p1[2]);

// Intersect two segments. The intersection, if it exists, is written
// to p (if not NULL), and 1 is returned. Else, zero is returned.
int g2d_line_segment_intersect_segment(const g2d_line_segment_t *sega, const g2d_line_segment_t *segb, double *p);

void g2d_line_segment_closest_point(const g2d_line_segment_t *seg, const double *q, double *p);
double g2d_line_segment_closest_point_distance(const g2d_line_segment_t *seg, const double *q);

////////////////////////////////////////////////////////////////////
// Polygons

zarray_t *g2d_polygon_create_data(double v[][2], int sz);

// Takes a polygon in either CW or CCW and modifies it (if necessary)
// to be CCW.
void g2d_polygon_make_ccw(zarray_t *poly);

// Return 1 if point q lies within poly.
int g2d_polygon_contains_point(const zarray_t *poly, double q[2]);

// Do the edges of the polygons cross? (Does not test for containment).
int g2d_polygon_intersects_polygon(const zarray_t *polya, const zarray_t *polyb);

// Does polya completely contain polyb?
int g2d_polygon_contains_polygon(const zarray_t *polya, const zarray_t *polyb);

// Is there some point which is in both polya and polyb?
int g2d_polygon_overlaps_polygon(const zarray_t *polya, const zarray_t *polyb);

#endif
