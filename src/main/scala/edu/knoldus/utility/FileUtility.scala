package edu.knoldus.utility

import java.nio.file.{Files, Paths}
import net.liftweb.json._

import edu.knoldus.model.Models.{ImageHeaderData, ImageObjects}


object FileUtility {
  implicit val formats = DefaultFormats


  def readImageHeaderJsonFile(path: String): List[ImageHeaderData] = {
    val fileJson: String = readFile(path)
    val imageHeader = parse(fileJson)
    imageHeader.extract[List[ImageHeaderData]]
  }

  private def readFile(path: String): String = {
    new String(Files.readAllBytes(Paths.get(path)))
  }

  def readImageObjectsJsonFile(path: String): List[ImageObjects] = {
    val fileJson = readFile(path)
    val imageObjects = parse(fileJson)
    imageObjects.extract[List[ImageObjects]]
  }
}
