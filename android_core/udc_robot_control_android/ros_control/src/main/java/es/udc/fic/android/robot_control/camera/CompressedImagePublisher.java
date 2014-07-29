/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * Modified version by amancio.diaz@gmail.com
 */
package es.udc.fic.android.robot_control.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.Size;
import com.google.common.base.Preconditions;
import es.udc.robotcontrol.utils.Constants;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.ros.internal.message.MessageBuffers;
import org.ros.message.Time;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 * Publishes preview frames.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
class CompressedImagePublisher implements RawImageListener {

    private final String IMAGE_QUEUE_NAME = Constants.TOPIC_IMAGE;
    private final String CAMERA_INFO_QUEUE_NAME = Constants.TOPIC_CAMERA_INFO;

  private final ConnectedNode connectedNode;
  private final Publisher<sensor_msgs.CompressedImage> imagePublisher;
  private final Publisher<sensor_msgs.CameraInfo> cameraInfoPublisher;

  private byte[] rawImageBuffer;
  private Size rawImageSize;
  private YuvImage yuvImage;
  private Rect rect;
  private ChannelBufferOutputStream stream;

  public CompressedImagePublisher(String robotName, ConnectedNode connectedNode) {
    this.connectedNode = connectedNode;
    String imageQueue = robotName + "/" + IMAGE_QUEUE_NAME + "/compressed";
    String infoQueue = robotName + "/" + CAMERA_INFO_QUEUE_NAME;
    imagePublisher = connectedNode.newPublisher(imageQueue, sensor_msgs.CompressedImage._TYPE);
    cameraInfoPublisher = connectedNode.newPublisher(infoQueue, sensor_msgs.CameraInfo._TYPE);
    stream = new ChannelBufferOutputStream(MessageBuffers.dynamicBuffer());
  }

  @Override
  public void onNewRawImage(byte[] data, Size size) {
    Preconditions.checkNotNull(data);
    Preconditions.checkNotNull(size);
    if (data != rawImageBuffer || !size.equals(rawImageSize)) {
      rawImageBuffer = data;
      rawImageSize = size;
      yuvImage = new YuvImage(rawImageBuffer, ImageFormat.NV21, size.width, size.height, null);
      rect = new Rect(0, 0, size.width, size.height);
    }

    Time currentTime = connectedNode.getCurrentTime();
    String frameId = "camera";

    sensor_msgs.CompressedImage image = imagePublisher.newMessage();
    image.setFormat("jpeg");
    image.getHeader().setStamp(currentTime);
    image.getHeader().setFrameId(frameId);

    Preconditions.checkState(yuvImage.compressToJpeg(rect, 20, stream));
    image.setData(stream.buffer().copy());
    stream.buffer().clear();

    imagePublisher.publish(image);

    sensor_msgs.CameraInfo cameraInfo = cameraInfoPublisher.newMessage();
    cameraInfo.getHeader().setStamp(currentTime);
    cameraInfo.getHeader().setFrameId(frameId);

    cameraInfo.setWidth(size.width);
    cameraInfo.setHeight(size.height);
    cameraInfoPublisher.publish(cameraInfo);
  }
}
