cmake_minimum_required(VERSION 2.8.3)
project(udc_robot_control_java)

find_package(catkin REQUIRED rosjava_core std_msgs geometry_msgs message_generation)


catkin_rosjava_setup()

add_message_files(
  FILES
  ActionCommand.msg  
  BateryStatus.msg  
  Engines.msg  
  Led.msg  
  RobotAction.msg
  AndroidSensor3.msg
  AndroidSensor4.msg
)

generate_messages(
  DEPENDENCIES
  std_msgs
  geometry_msgs
)



catkin_package(CATKIN_DEPENDS message_runtime)


