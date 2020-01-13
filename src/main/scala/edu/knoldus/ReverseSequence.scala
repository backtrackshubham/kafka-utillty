package edu.knoldus

import java.io.File
import java.nio.file.Files
import java.util.UUID.randomUUID

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FileSystem, Path}
import org.apache.hadoop.io.{BytesWritable, SequenceFile, Text}
import org.apache.hadoop.io.SequenceFile.CompressionType
import org.apache.hadoop.io.compress.DefaultCodec

object ReverseSequence extends App {
  val basePath = "/home/shubham/Desktop/Desktop/sequence-read/failing/9e463bfa-dd13-4945-bf84-00d4900579d5/47ea653a-32ab-11ea-b4f2-00e04c680001/"
  val leftFolder = s"${basePath}Left/"
  val rightFolder = s"${basePath}Right/"
  val filesLeft = new File(leftFolder)
  val filesRight = new File(rightFolder)
  val leftFiles = filesLeft.list().toList.sorted.take(10)
  val rightFiles = filesRight.list().toList.sorted.take(10)
  val leftFilesRev = leftFiles.reverse
  val rightFilesRev = rightFiles.reverse
  println(leftFiles)
  println(rightFiles)
  val conf = new Configuration
   System.setProperty("HADOOP_USER_NAME", "shubham")
//  System.setProperty("HADOOP_USER_NAME", "hadoop")
   conf.set("fs.defaultFS", "hdfs://192.168.11.103:4569")
//  conf.set("fs.defaultFS", "hdfs://10.2.5.4:9000") //dev
  //  conf.set("fs.defaultFS", "hdfs://10.3.4.4:9000") //prod
  conf.set("dfs.replication", "1")
  val fs = FileSystem.get(conf)

  val unitId = "9e463bfa-dd13-4945-bf84-00d4900579d5"
  //  val imageId = "d9ed6016-1ee4-11ea-b990-00044be64e82"

  //  val unitId = randomUUID.toString
  val imageId = "47ea653a-32ab-11ea-b4f2-00e04c680002"

  val leftSeqFile = s"/kerb/images/$unitId/$imageId/$imageId-L"
  val rightSeqFile = s"/kerb/images/$unitId/$imageId/$imageId-R"

  val leftPath = new Path(leftSeqFile)
  val rightPath = new Path(rightSeqFile)
  val leftFile: FSDataOutputStream = fs.create(leftPath)
  val rightFile: FSDataOutputStream = fs.create(rightPath)
  val leftWriter = SequenceFile.createWriter(conf, leftFile, new Text().getClass, new BytesWritable().getClass, CompressionType.NONE, new DefaultCodec)
  val rightWriter = SequenceFile.createWriter(conf, rightFile, new Text().getClass, new BytesWritable().getClass, CompressionType.NONE, new DefaultCodec)

  leftFiles.zipWithIndex foreach {
    case (leftFileName, index) =>
      val leftFile = new File(s"$leftFolder$leftFileName")
      val rightFile = new File(s"$rightFolder${rightFiles(index)}")
      val leftBytes = Files.readAllBytes(leftFile.toPath)
      val rightBytes = Files.readAllBytes(rightFile.toPath)
      val keyLeft: Text = new Text(leftFiles(index).replace("47ea653a-32ab-11ea-b4f2-00e04c680001", imageId))
      val valueLeft: BytesWritable = new BytesWritable(leftBytes)
      val keyRight: Text = new Text(rightFiles(index).replace("47ea653a-32ab-11ea-b4f2-00e04c680001", imageId))
      val valueRight: BytesWritable = new BytesWritable(rightBytes)
      leftWriter.append(keyLeft, valueLeft)
      rightWriter.append(keyRight, valueRight)
  }
  println(s"================ Completed Closing writer =================")
  println(s"================ Final JSON =================\n")
  val json = s"""{ "imagesDirUrl" : "${leftSeqFile}", "imageUUID" : "${imageId}", "unitId" : "${unitId}", "imagesCount" : 599 }"""
  println(s"$json")
  println(s"\n================ Final JSON =================")
}
