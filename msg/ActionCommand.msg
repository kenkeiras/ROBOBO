# Define un mensaje con ordenes para el robot
# Las posibles ordenes incluyen:
#  * Parar o arrancar publishers
#  * Enceder o apagar leds
#  * Configurar el modo de accion de los motores
# Ver clase CommandListener para una descripcion completa de los posibles comandos y sus efectos
#
int32 CMD_RESET           = 0 # Resetea el estado del robot, detiene motores y apaga leds
int32 CMD_HARD_RESET      = 1 # Resetea el estado del robot, detiene motores apaga leds y desactiva todos los publishers
int32 CMD_START_PUBLISHER = 2 # Arranca un publisher (ver tabla de publishers)
int32 CMD_STOP_PUBLISHER  = 3 # Detine un publisher
int32 CMD_SET_LEDS        = 4 # Fija los leds
int32 CMD_SET_ENGINES     = 5 # Fija los motores



int32 PUBLISHER_BATERY      = 0   # Publisher de bateria
int32 PUBLISHER_GPS         = 1   # Publisher de posicion (no solo GPS)
int32 PUBLISHER_ACCEL       = 2   # Publisher de acelerometro
int32 PUBLISHER_GYRO        = 3   # Publisher de giroscopio
int32 PUBLISHER_ANGULAR     = 4   # Publisher de velocidad angular (quat)
int32 PUBLISHER_IMU         = 5   # Publisher de Inertial Measure Unit (ACCEL + GYRO + ANGULAR)
int32 PUBLISHER_MAG         = 6   # Publisher de magnetometro (brujula)
int32 PUBLISHER_PROXIMITY   = 7   # Publisher de proximidad
int32 PUBLISHER_TEMPERATURE = 8   # Publisher de temperatura
int32 PUBLISHER_ILLUMINANCE = 9   # Publisher de iluminacion
int32 PUBLISHER_PRESSURE    = 10  # Publisher de presion de fluidos (barometro)
int32 PUBLISHER_AUDIO       = 11  # Publisher de audio
int32 PUBLISHER_VIDEO       = 12  # Publisher de video


Header  header

int32  command  # Comando. Ver constantes CMD_xxx
int32 publisher # Publisher para arrancar/detener
Engines engines # Motores
Led[] leds      # Array de leds

int32 param0       # Parametros opciones
int32 param1
int32 param2
string string0
string string1
string string2
