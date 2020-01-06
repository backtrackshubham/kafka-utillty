package edu.knoldus

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FileSystem, Path}
import org.apache.hadoop.io.SequenceFile.CompressionType
import org.apache.hadoop.io.compress.DefaultCodec
import org.apache.hadoop.io.{BytesWritable, SequenceFile, Text}
import java.util.UUID.randomUUID

object InvalidSequenceFileHelper extends App {
  val counter = 599
  val conf = new Configuration
  System.setProperty("HADOOP_USER_NAME", "shubham")
  conf.set("fs.defaultFS", "hdfs://192.168.11.103:4569")
  conf.set("dfs.replication", "1")
  val fs = FileSystem.get(conf)

//  val unitId = "amz12141-56a1-4446-ae6d-9b1700b8bdc0"
//  val imageId = "d9ed6016-1ee4-11ea-b990-00044be64e82"

  val unitId = randomUUID.toString
  val imageId = randomUUID.toString

  val leftSeqFile = s"/kerb/images/$unitId/$imageId/$imageId-L"
  val rightSeqFile = s"/kerb/images/$unitId/$imageId/$imageId-R"

  val leftPath = new Path(leftSeqFile)
  val rightPath = new Path(rightSeqFile)
  val leftFile: FSDataOutputStream = fs.create(leftPath)
  val rightFile: FSDataOutputStream = fs.create(rightPath)
  val leftWriter = SequenceFile.createWriter(conf, leftFile, new Text().getClass, new BytesWritable().getClass, CompressionType.NONE, new DefaultCodec)
  val rightWriter = SequenceFile.createWriter(conf, rightFile, new Text().getClass, new BytesWritable().getClass, CompressionType.NONE, new DefaultCodec)
  (0 to counter) foreach { index =>
    val fileBytes = WebCamTester.getImage
    val keyNameLeft = s"$imageId-$index%05d-L.jpg"
    val keyNameRight = s"$imageId-$index%05d-R.jpg"
    val keyLeft: Text = new Text(keyNameLeft)
    val valueLeft: BytesWritable = new BytesWritable(fileBytes)
    val keyRight: Text = new Text(keyNameRight)
    val valueRight: BytesWritable = new BytesWritable(fileBytes)
    leftWriter.append(keyLeft, valueLeft)
    rightWriter.append(keyRight, valueRight)
//    Thread.sleep(100)
  }

  println(s"================ Completed Closing writer =================")

  leftWriter.hsync()
  leftWriter.close()
  rightWriter.hsync()
  rightWriter.close()
  WebCamTester.closeCam
}
