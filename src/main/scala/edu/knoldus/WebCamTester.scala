package edu.knoldus

import java.awt.Dimension
import java.io.{ByteArrayOutputStream, File}

import com.github.sarxos.webcam.Webcam
import javax.imageio.ImageIO

object WebCamTester{
  val webcam = Webcam.getDefault
  val dim = new Dimension(640, 480)
  webcam.setViewSize(dim)
  webcam.open
  def getImage: Array[Byte] = {
    val baos:ByteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(webcam.getImage, "jpg", baos);
    baos.toByteArray;
  }
//  ImageIO.write(webcam.getImage., "JPEG", new File("/home/freaks/Desktop/hello-world.jpg"))
}
