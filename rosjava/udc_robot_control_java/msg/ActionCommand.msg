# Define un mensaje con ordenes para el robot
# Las posibles ordenes incluyen:
#  * Parar o arrancar publishers
#  * Enceder o apagar leds
#  * Configurar el modo de accion de los motores
# Ver clase CommandListener para una descripcion 
# completa de los posibles comandos y sus efectos
#

# Resetea el estado del robot, 
# detiene motores y apaga leds
int32 CMD_RESET           = 0 
# Resetea el estado del robot, detiene motores 
# apaga leds y desactiva todos los publishers
int32 CMD_HARD_RESET      = 1 
# Arranca un publisher (ver tabla de publishers)
int32 CMD_START_PUBLISHER = 2 
# Detine un publisher
int32 CMD_STOP_PUBLISHER  = 3 
# Fija los leds
int32 CMD_SET_LEDS        = 4 
# Fija los motores
int32 CMD_SET_ENGINES     = 5 

# Publisher de bateria
int32 PUBLISHER_BATERY                      = 0   
# Publisher de posicion (no solo GPS)
int32 PUBLISHER_GPS                         = 1   
# Publisher de Inertial Measure Unit (ACCEL + GYRO + ANGULAR)
int32 PUBLISHER_IMU                         = 2   
# Publisher de acelerometro
int32 PUBLISHER_ACCELEROMTER                = 3   
# Publisher de magnetometro (brujula)
int32 PUBLISHER_MAGNETIC_FIELD              = 4   
# Publisher de giroscopio
int32 PUBLISHER_GYROSCOPE                   = 5   
# Publisher de iluminacion
int32 PUBLISHER_LIGHT                       = 6   
# Publisher de presion de fluidos (barometro)
int32 PUBLISHER_PRESSURE                    = 7   
# Publisher de proximidad
int32 PUBLISHER_PROXIMITY                   = 8   
# Publisher de gravedad
int32 PUBLISHER_GRAVITY                     = 9   
# Publisher de gravedad
int32 PUBLISHER_LINEAL_ACCELERATION         = 10  
# Publisher de vector de rotacion
int32 PUBLISHER_ROTATION_VECTOR             = 11  
# Publisher de orientacion
int32 PUBLISHER_ORIENTATION                 = 12  
# Publisher de humedad relativa
int32 PUBLISHER_RELATIVE_HUMIDITY           = 13  
# Publisher de temperatura
int32 PUBLISHER_AMBIENT_TEMPERATURE         = 14  
# Publisher de campo magnetico sin calibrar
int32 PUBLISHER_MAGNETIC_FIELD_UNCALIBRATED = 15  
# Publisher de vector de rotacion para juegos
int32 PUBLISHER_GAME_ROTATION_VECTOR        = 16  
# Publisher de giroscopio sin calibrar
int32 PUBLISHER_GYROSCOPE_UNCALIBRATED      = 17  
# Publisher de audio
int32 PUBLISHER_AUDIO                       = 18  
# Publisher de video
int32 PUBLISHER_VIDEO                       = 19  

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
