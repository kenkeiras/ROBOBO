# Defines a message with orders for the robot
# Possible orders include:
#  * Start/stop publishers
#  * Turn on/off leds
#  * Configurate the engine operation mode
# See the CommandListener class for a complete description
# of the possible commands and it's effects.
#

# Reset the robot state, stop engines and turn leds off
int32 CMD_RESET           = 0
# Reset the robot state, stop engines, turn off leds and stop all the publishers
int32 CMD_HARD_RESET      = 1
# Start a publishers (see publisher table)
int32 CMD_START_PUBLISHER = 2
# Stop a publisher
int32 CMD_STOP_PUBLISHER  = 3
# Set the leds
int32 CMD_SET_LEDS        = 4
# Set the engine action
int32 CMD_SET_ENGINES     = 5

# Battery publisher
int32 PUBLISHER_BATTERY                     = 0
# Position publisher (not just GPS)
int32 PUBLISHER_GPS                         = 1
# Inertial Measure Unit publisher (ACCEL + GYRO + ANGULAR)
int32 PUBLISHER_IMU                         = 2
# Accelerometer publisher
int32 PUBLISHER_ACCELEROMTER                = 3
# Magnetometer publisher (compass)
int32 PUBLISHER_MAGNETIC_FIELD              = 4
# Gyroscope publisher
int32 PUBLISHER_GYROSCOPE                   = 5
# Illumination publisher
int32 PUBLISHER_LIGHT                       = 6
# Fluid pressure publisher (barometer)
int32 PUBLISHER_PRESSURE                    = 7
# Proximity publisher
int32 PUBLISHER_PROXIMITY                   = 8
# Gravity publisher
int32 PUBLISHER_GRAVITY                     = 9
# Lineal acceleration publisher
int32 PUBLISHER_LINEAL_ACCELERATION         = 10
# Rotation vector publisher
int32 PUBLISHER_ROTATION_VECTOR             = 11
# Orientation publisher
int32 PUBLISHER_ORIENTATION                 = 12
# Relative humidity publisher
int32 PUBLISHER_RELATIVE_HUMIDITY           = 13
# Ambient temperature publisher
int32 PUBLISHER_AMBIENT_TEMPERATURE         = 14
# Uncalibrated magnetic field publisher
int32 PUBLISHER_MAGNETIC_FIELD_UNCALIBRATED = 15
# Rotation vector (for games) publisher
int32 PUBLISHER_GAME_ROTATION_VECTOR        = 16
# Uncalibrated gyroscope publisher
int32 PUBLISHER_GYROSCOPE_UNCALIBRATED      = 17
# Audio publisher
int32 PUBLISHER_AUDIO                       = 18
# Video publisher
int32 PUBLISHER_VIDEO                       = 19

Header  header

int32  command  # Command. See CMD_xxx constants
int32 publisher # Publisher to be started/stopped
Engines engines # Engines
Led[] leds      # Led array

# Optional parameters
int32 param0
int32 param1
int32 param2
string string0
string string1
string string2
