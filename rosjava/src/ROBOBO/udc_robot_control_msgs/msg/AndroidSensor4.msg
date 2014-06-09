# This msg defines an Android Sensor msg used for vectors of 4 elements
# Represents a (w,x,y,z) tuple for 4 dimension sensors (API level 18 defines an optional extra accuracy)
Header header
geometry_msgs/Quaternion data
float64[] extra