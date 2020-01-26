package edu.knoldus

import java.io.InputStream

import java.time.Instant

import edu.knoldus.model._
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import org.apache.commons.io.IOUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration


object DummyData {

  val rnd = new scala.util.Random

  def getRandomInt(start: Int, end: Int): Int = start + rnd.nextInt((end - start) + 1)

  val imageHeader = ImageHeaderData(s"",
    "",
    "",
    "ipAddress",
    System.currentTimeMillis(),
    0.0f,
    false,
    1,
    2,
    3,
    4,
    6,
    5,
    9,
    None,
    0)

  val gpsData = GPSData(
    "gpsId",
    None,
    0,
    FileUtility.GPS_DATE_FORMATTER.format(Instant.now()),
    Coordinates(56, 36.9658),
    "N",
    Coordinates(56, 36.9658),
    "W",
    5.6,
    36.96,
    false,
    None,
    "None"
  )
  val imuData = IMUData("imuId",
    0,
    0,
    None,
    LinAcc(1, 2, 3),
    Magnetometer(7, 8, 9),
    Gyro(4, 5, 6),
    Quaternion(9, 6, 3, 8),
    "None",
    None
  )
}


object BombardierData extends App {

  implicit val formats: DefaultFormats.type = DefaultFormats
  val lambda = (x: Int, y: Int) => (x + y, x - y, x * y, x / y)
  val uniqueObjects = List("Person", "Car", "Building", "Bus", "Pole", "Bag", "Drone", "Truck", "Person", "Person")

  val imagesPerCamera: List[InputStream] = FutureHelper.functionalFileList.zipWithIndex.flatMap {
    case (element, _) =>
      (1 to ConfigConstants.imagesPerCamera).map(_ => element)
  }

  val cameraId = "ASD$1231241"
  val unitIds = List(java.util.UUID.randomUUID.toString)
  val imageHeaderData: ImageHeaderData = DummyData.imageHeader
  val gpsData: GPSData = DummyData.gpsData
  val imuData: IMUData = DummyData.imuData

  def publishImageHeader(unitId: String): Future[List[(ObjectDataMessage, String)]] = Future {
    val objectDetector = java.util.UUID.randomUUID.toString
    List(unitId).flatMap(unitId => {
      println(s"publishing for $unitId")
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.map { case (file, index: Int) =>
//        val byteArray = Files.readAllBytes(file.toPath) //excess overhead
        val byteArray = IOUtils.toByteArray(file) //excess overhead
        val timestamp = System.currentTimeMillis()
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = timestamp, imageId = f"$imageId-$index%05d", unitId = unitId, imageCounter = index)))
        DataProducer.writeImageToKafka(ConfigConstants.imageTopic, s"$unitId-$imageId-L", f"${unitId}_$imageId-$index%05d-L.jpg", byteArray)
        DataProducer.writeImageToKafka(ConfigConstants.imageTopic, s"$unitId-$imageId-R", f"${unitId}_$imageId-$index%05d-R.jpg", byteArray)
        Thread.sleep(100)
        publishImageObjects(unitId, f"$imageId-$index%05d", imageId, objectDetector, index)
      }
    })
  }

  def publishGPSData(unitId: String) = Future {
    List(unitId) foreach { camera =>
      imagesPerCamera.foreach { _ =>
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera,
          write(gpsData.copy(timestampLinux = System.currentTimeMillis(),
            latitude = Coordinates((System.currentTimeMillis() % 360).toInt, System.currentTimeMillis() % 60), latitudeNS = if (System.currentTimeMillis() % 2 == 0) "N" else "S",
            longitude = Coordinates((System.currentTimeMillis() % 360).toInt, System.currentTimeMillis() % 60), longitudeEW = if (System.currentTimeMillis() % 2 == 0) "E" else "W",
            unitId = camera,
            timestampGPS = FileUtility.GPS_DATE_FORMATTER.format(Instant.now()))))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData(unitId: String) = Future {
    List(unitId) foreach { camera =>
      imagesPerCamera.foreach { _ =>
        (1 to 10) foreach { _ =>
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, camera, write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          Thread.sleep(10)
        }
      }
    }
  }

  def publishImageObjects(unitId: String, imageId: String, imageUUID: String, objectDetector: String, counter: Int): (ObjectDataMessage, String) = {
    val data = if (counter % 5 == 0) { //every fifth image would be empty
      ObjectDataMessage(
        ImageMessage(Array.empty[Int], true, imageId, imageUUID, s"$imageId.jpg", s"$imageId.jpg", unitId),
        None,
        "yolo3", Instant.now().toEpochMilli
      )
    } else {
      ObjectDataMessage(
        ImageMessage(Array.empty[Int], false, imageId, imageUUID, s"$imageId.jpg", s"$imageId.jpg", unitId),
        Some((0 to (counter % 10)).toList.map(value => ObjectData(counter + value, value, uniqueObjects(value), 3.45, BoundingBox(1, 2, 3, 4), 2.36))),
        "yolo3", Instant.now().toEpochMilli
      )
    }
    //    DataProducer.writeToKafka(ConfigConstants.imageObjects, imageId, write(data))
    (data, imageId)
  }

  def publishTrackingData(trackingData: List[TrackingData]) = {
    trackingData.foreach(trackData => {
      //      DataProducer.writeToKafka(ConfigConstants.trackingData, trackData.unitId, write(trackData))
    })
  }

  def generateTrackingData(objects: List[(ObjectDataMessage, String)]): Future[List[TrackingData]] = Future {
    (objects.zipWithIndex flatMap { case ((imgObject, imageId), index) =>
      if (imgObject.ImageData.imageEmpty) {
        None
      } else {
        Some(imgObject.imageObjects.get.map(imageObjectData => {
          TrackingData(imgObject.ImageData.unitId,
            s"${imgObject.ImageData.imageUUID}-T-${imageObjectData.objId}",
            imageObjectData.objLabelDefinition,
            imgObject.ImageData.imageUUID,
            if (index % 2 == 0) 0.6 else 0.3,
            (1 to new java.util.Random(10).nextInt()).toList map (index2 => {
              Occurrence(s"$imageId",
                imgObject.timestamp,
                BoundingBox(4, DummyData.getRandomInt(0, 360), 5, 9),
                if (index % 2 == 0) 0.6 else 0.3, 2.36)
            }))
        }))
      }
    }).flatten
  }

  def publishAll(unitId: String): Future[Unit] = for {
    objectList <- publishImageHeader(unitId)
    imgObject <- generateTrackingData(objectList)
  } yield publishTrackingData(imgObject)

  val units = (1 to ConfigConstants.numCameras).toList.map(_ => java.util.UUID.randomUUID().toString)

  println("================================")
  println(units.mkString("\n"))
  println("================================")

  val res: Future[List[Unit]] = Future.sequence(units.map(unitId => {
    println(s"======================= Going to publish $unitId")
    Future.sequence(List(publishGPSData(unitId), publishIMUData(unitId), publishAll(unitId)))
  })).map(_.flatten)

//  val res = Future.sequence(List(publishGPSData(u), publishIMUData, publishAll))

  Await.ready(res.map(_ => {
    println("================================ Process completed")
    0
  }), Duration.Inf)
}
