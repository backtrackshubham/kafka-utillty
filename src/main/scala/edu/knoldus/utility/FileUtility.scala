package edu.knoldus.utility

import java.nio.file.{Files, Paths}
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import edu.knoldus.model.{GPSData, IMUData, ImageAggregate2, ImageHeaderData, ImageObjects, TrackingComplete}
import net.liftweb.json._


object FileUtility {
  implicit val formats = DefaultFormats

  val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS zzz"
  val GPS_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.of("UTC"))
  def readImageHeaderJsonFile(path: String): List[ImageHeaderData] = {
    val fileJson: String = readFile(path)
    val imageHeader = parse(fileJson)
    imageHeader.extract[List[ImageHeaderData]]
  }

  def readFile(path: String): String = {
    new String(Files.readAllBytes(Paths.get(path)))
  }

  def readImageObjectsJsonFile(path: String): List[ImageObjects] = {
    val fileJson = readFile(path)
    val imageObjects = parse(fileJson)
    imageObjects.extract[List[ImageObjects]]
  }

  def readGPSDataJsonFile(path: String): List[GPSData] = {
    val fileJson = readFile(path)
    val imageObjects = parse(fileJson)
    imageObjects.extract[List[GPSData]]
  }

  def readIMUDataJsonFile(path: String): List[IMUData] = {
    val fileJson = readFile(path)
    val imageObjects = parse(fileJson)
    imageObjects.extract[List[IMUData]]
  }

  def readTrackCompleteJsonFile(path: String): List[TrackingComplete] = {
    val fileJson = readFile(path)
    val imageObjects = parse(fileJson)
    imageObjects.extract[List[TrackingComplete]]
  }



  def readImageAggregate2File(path: String): List[ImageAggregate2] = {
    val fileJson = readFile(path)
    val imageObjects = parse(fileJson)
    imageObjects.extract[List[ImageAggregate2]]
  }
}
