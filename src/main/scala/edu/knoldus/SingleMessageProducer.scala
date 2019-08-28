package edu.knoldus

object SingleMessageProducer extends App {
  FutureHelper.publishSingleImageHeader
  FutureHelper.publishSingleGPSData
  FutureHelper.publishSingleIMUData
  FutureHelper.publishSingleImageObjects

  Thread.sleep(10000)
}
