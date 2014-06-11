##############################################################################
# CMake
##############################################################################

cmake_minimum_required(VERSION 2.8.3)
project(udc_robot_control_java)

##############################################################################
# Catkin
##############################################################################

find_package(catkin REQUIRED COMPONENTS message_generation std_msgs 
             geometry_msgs udc_robot_control_msgs)

## System dependencies are found with CMake's conventions
find_package(Boost REQUIRED COMPONENTS system)

# Set the gradle targets you want catkin's make to run by default, e.g.
#   catkin_rosjava_setup(installApp)
# Note that the catkin_create_rosjava_xxx scripts will usually automatically
# add tasks to this for you when you create subprojects.
catkin_rosjava_setup(publishMavenJavaPublicationToMavenRepository)

catkin_package()


##############################################################################
# Installation
##############################################################################

# Change this to match the maven group name you have specified in the 
# allprojects closure the root build.gradle
install(DIRECTORY ${CATKIN_DEVEL_PREFIX}/${CATKIN_GLOBAL_MAVEN_DESTINATION}/com/github/rosjava/${PROJECT_NAME}/ 
        DESTINATION ${CATKIN_GLOBAL_MAVEN_DESTINATION}/com/github/rosjava/${PROJECT_NAME})
