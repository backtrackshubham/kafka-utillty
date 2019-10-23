package edu.knoldus

import java.io.File

object ImageHeaderHelper extends App {
  val dir = "/home/freaks/SHUBHAM/Projects/KERB/object-detector-test"
  val leftFilesDir = "/home/freaks/SHUBHAM/Projects/KERB/object-detector-test/Left"
  val rightFilesDir = "/home/freaks/SHUBHAM/Projects/KERB/object-detector-test/Right"
  val leftPath = new File(leftFilesDir)
  val rightPath = new File(rightFilesDir)
  val leftFileNames = leftPath.listFiles() map (_.getAbsolutePath.replace(s"$leftFilesDir/", ""))
  val rightFileNames = rightPath.listFiles() map (_.getAbsolutePath.replace(s"$rightFilesDir/", ""))
  println(leftFileNames.length)
  println(rightFileNames.length)

}
