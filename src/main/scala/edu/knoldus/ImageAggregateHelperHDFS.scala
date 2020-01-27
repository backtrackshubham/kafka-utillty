package edu.knoldus

import java.io.{File, FileOutputStream, PrintWriter}
import java.time.Instant

import edu.knoldus.utility.FileUtility
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}
import org.apache.hadoop.io.{BytesWritable, SequenceFile, Text}


object Utils {

  val conf = new Configuration
  System.setProperty("HADOOP_USER_NAME", "hadoop")
  conf.set("fs.defaultFS", "hdfs://10.2.5.4:9000") //dev
  //  conf.set("fs.defaultFS", "hdfs://10.3.4.4:9000") //prod
  conf.set("dfs.replication", "1")
  val fs = FileSystem.get(conf)

  def folderToJson(folder: FileStatus, counter: Int = 1799): (String, String) = {
    val path = folder.getPath.toString
    println(s"======== $path")
    val splited: Array[String] = path.split("/kerb/images/")
    val unitAndImage: String = splited(1)

    val splitUnitAndImage: Array[String] = unitAndImage.split("/")

    val unitId: String = splitUnitAndImage(0)
    val imageId: String = splitUnitAndImage(1)
    val hdfsCreationTime = FileUtility.GPS_DATE_FORMATTER.format(Instant.ofEpochMilli(folder.getModificationTime))

    val (leftFileCount, rightFileCount) = getLeftRightSequenceFile(path, imageId)


    (unitId,s"""{"imageUUID": "${imageId}", "imagesDirUrl": "/kerb/images/${unitId}/${imageId}/${imageId}-L", "unitId": "${unitId}","firstImageId":"${imageId}-00000", "imagesLeftFolder" : $leftFileCount, "imagesRightFolder" : $rightFileCount, "imagesCount":$counter, "hdfsCreationTime" :"$hdfsCreationTime"}""")
  }

  def getLeftRightSequenceFile(path: String, imageUUID: String): (Int, Int) = {
    var leftImageCount = 0
    var rightImageCount = 0
    val splited = path.split("hdfs://10.2.5.4:9000")(1)
    println(s"splited $splited")
    val leftFileUrl = s"$splited/$imageUUID-L"
    val rightFileUrl = s"$splited/$imageUUID-R"

    leftImageCount = if(!fs.exists(new Path(leftFileUrl))){
      0
    } else {
      val key: Text = new Text()
      val value: BytesWritable = new BytesWritable()
      try{
      val reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(new Path(leftFileUrl)))

      try{
        while(reader.next(key,value)){
          leftImageCount = leftImageCount + 1
        }
      } catch {
        case exception: Exception =>
          println(s"$exception occurred")
      }
      reader.close()
      } catch {
        case exception: Exception =>
          println(s"$exception occurred")
      }
      leftImageCount
    }

    rightImageCount = if(!fs.exists(new Path(rightFileUrl))){
      0
    } else {
      val key: Text = new Text()
      val value: BytesWritable = new BytesWritable()
      try {
        val reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(new Path(rightFileUrl)))

        try {
          while (reader.next(key, value)) {
            rightImageCount = rightImageCount + 1
          }
        } catch {
          case exception: Exception =>
            println(s"$exception occurred")
        }
        reader.close()
      } catch {
        case exception: Exception =>
          println(s"$exception occurred")
      }
      rightImageCount
    }

    (leftImageCount, rightImageCount)
  }

}

object ImageAggregateHelperHDFS extends App {
  import Utils._


  val base = "/kerb/images/"
  //superBowl units
  val units = List("camera01-proc-gamb-9409-fc9562289952",
    "camera02-proc-gamb-b80c-d9efd4eaab5f",
    "camera03-proc-gamb-b739-6af527e0b51f",
    "camera04-proc-gamb-aa5f-53bd905f8dde",
    "camera05-proc-gamb-b9d3-a6a178336b41")

  val unitPath = units.map(unit => s"$base$unit")

  val allFiles: List[FileStatus] = unitPath.flatMap{ unit =>
    println("\n\n==================\n\n")
    val files = fs.listStatus(new Path(unit))
    println("=========================================")
    println(s" $unit ${files.length}")
    println("=========================================")
    files.toList
  }.sortBy(_.getModificationTime)

  val writer  = new PrintWriter("/home/shubham/Desktop/hdfs-super-bowl-image-aggregate.json", "UTF-8");

  allFiles.foreach(status => {
    val(x,y) = folderToJson(status)
    println(y)
    writer.println(y)
    println()
  })
  writer.close();
  println(allFiles.length)


}
