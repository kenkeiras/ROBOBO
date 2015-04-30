package es.udc.fic.robobo.rosWrapper.managers.listeners;

import es.udc.robotcontrol.utils.Constants;
import sensor_msgs.CompressedImage;

public class CompressedImageListenerManager extends ListenerManager<CompressedImage> {

    public CompressedImageListenerManager(String robotName) {
        super(robotName, Constants.TOPIC_IMAGE + "/compressed", CompressedImage._TYPE);
    }
}
