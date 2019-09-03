package edu.knoldus.hdfsutils

import java.io.{File, FileOutputStream, OutputStream}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{BytesWritable, SequenceFile, Text}

object ConnectionProvider {
  val conf = new Configuration
  System.setProperty("HADOOP_USER_NAME", "freaks")
  conf.set("fs.defaultFS", "hdfs://localhost:4569")
  conf.set("dfs.replication", "1")
  val fs = FileSystem.get(conf)
  val directory = "/kerb/image"
  val pathSeq = new Path(s"$directory/fa28239a-813d-481e-a0e5-7955d31f27ff")
  val reader: SequenceFile.Reader = new SequenceFile.Reader(fs, pathSeq, conf)
  val key: Text = new Text()
  val value: BytesWritable = new BytesWritable()



  def read: Unit =   while (reader.next(key, value)) {
    val tosave = new File(s"/home/freaks/Desktop/sequence-read/${key.toString}")
    val os: OutputStream = new FileOutputStream(tosave)
    os.write(value.getBytes)
    os.close()
  }

  def read(fileName: String) = {

    reader.next(new Text(fileName), value)
    val tosave = new File(s"/home/freaks/Desktop/sequence-read/single-file/${fileName}")
    val os: OutputStream = new FileOutputStream(tosave)
    os.write(value.getBytes)
    os.close()
  }

}

object SequenceFileReader extends App {

  ConnectionProvider.read
//  ConnectionProvider.read("389bf49a-1717-429f-b8c7-856e8873a348-0-L.png")
}
