package es.udc.fic.robobo.rosWrapper.listenerHandlers;

import org.ros.message.MessageListener;

import udc_robot_control_msgs.AprilTag;

/**
 * Convenience interface for clases managing updates on found AprilTags.
 *
 * Doesn't add anything on top of a specialized MessageListener interface.
 *
 */
public interface AprilTagHandler extends MessageListener<AprilTag> {}
