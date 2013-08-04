cmake_minimum_required(VERSION 2.8.3)
project(udc_robot_control_java)

find_package(catkin REQUIRED rosjava_core std_msgs message_generation)

catkin_rosjava_setup()

catkin_package(CATKIN_DEPENDS message_runtime)

add_message_files(
  FILES
  ActionCommand.msg  
  BateryStatus.msg  
  Engines.msg  
  Led.msg  
  RobotAction.msg
)


