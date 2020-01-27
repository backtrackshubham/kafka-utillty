package edu.knoldus

import java.io.InputStream

import net.liftweb.json.DefaultFormats
import org.apache.commons.io.IOUtils


object FutureHelper {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val leftImages = s"images/real-images/left/"
  val rightImages = s"images/real-images/right/"

  val lam: (String, String) => Array[Byte] = (path: String, fileName: String) => {
    val fileInJar = getClass().getResourceAsStream(s"/$path$fileName")
    IOUtils.toByteArray(fileInJar)
  }
  val leftFileNames: String => Array[Byte] = lam(leftImages, _)
  val rightFileNames: String => Array[Byte] = lam(rightImages, _)
  val files = (0 to 29).toList.map(index => s"$index.jpg")

  val functionalFileListLeft: List[Array[Byte]] = files.map(leftFileNames)
  val functionalFileListRight: List[Array[Byte]] = files.map(rightFileNames)

}
