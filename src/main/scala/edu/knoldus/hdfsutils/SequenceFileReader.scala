package edu.knoldus.hdfsutils

import java.io.{File, FileOutputStream, OutputStream}
import java.util.UUID

import edu.knoldus.model.ImageAggregated
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{BytesWritable, SequenceFile, Text}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


trait HDFSConnectionFactory {
  val conf: Configuration
  val fs: FileSystem

  def readPathAndSaveToDir(aggregator: ImageAggregated, filesSaved: Int = 0 ): Int
}

object ConnectionProvider extends HDFSConnectionFactory {
  val conf = new Configuration
  System.setProperty("HADOOP_USER_NAME", "freaks")
  conf.set("fs.defaultFS", "hdfs://localhost:4569")
  conf.set("dfs.replication", "1")
  val fs = FileSystem.get(conf)
  val key: Text = new Text()
  val value: BytesWritable = new BytesWritable()

  def readPathAndSaveToDir(aggregator: ImageAggregated, filesSaved:  Int = 0 ): Int = {
    val leftFileUrl = s"${aggregator.imagesURL}/${aggregator.imageUUID}-L"
    val rightFileUrl = s"${aggregator.imagesURL}/${aggregator.imageUUID}-R"
    recursiveRead(leftFileUrl, aggregator.imageUUID, true)
    recursiveRead(rightFileUrl, aggregator.imageUUID, false)

  }

  private def recursiveRead(path: String, UUID: String, isLeft: Boolean): Int = {
    val pathSeq = new Path(path)
    val reader: SequenceFile.Reader = new SequenceFile.Reader(fs, pathSeq, conf)
    val key: Text = new Text()
    val value: BytesWritable = new BytesWritable()
    while (reader.next(key, value)) {
      val dir = s"/home/freaks/Desktop/sequence-read/$UUID/${if(isLeft) "Left" else "Right"}"
      val dirFile = new File(dir)
      if(!dirFile.exists()){
        dirFile.mkdirs()
      }
      val filePath = s"$dir/${key.toString}"
      println(filePath)
      val tosave = new File(filePath)
      val os: OutputStream = new FileOutputStream(tosave)
      os.write(value.getBytes)
      os.close()
    }
    0
  }
}
