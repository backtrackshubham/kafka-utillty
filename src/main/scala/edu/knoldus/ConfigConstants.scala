package edu.knoldus

object ConfigConstants {
  val imageHeaderTopic: String = ConfigProvider.getEnvString("kafka-config.topics.image-header")
  val imageTopic: String = ConfigProvider.getEnvString("kafka-config.topics.image-message")
  val imageHeaderTopicProcessed: String = ConfigProvider.getEnvString("kafka-config.topics.image-header-processed")
  val imageIMUTopicPublish: String = ConfigProvider.getEnvString("kafka-config.topics.image-imu-publish")
  val imageIMUTopicSubscribe: String = ConfigProvider.getEnvString("kafka-config.topics.image-imu-subscribe")
  val imageGPSTopicPublish: String = ConfigProvider.getEnvString("kafka-config.topics.image-gps-publish")
  val imageGPSTopicSubscribe: String = ConfigProvider.getEnvString("kafka-config.topics.image-gps-subscribe")
  val imageGPSData: String = ConfigProvider.getEnvString("kafka-config.topics.image-gps-data")
  val imageIMUData: String = ConfigProvider.getEnvString("kafka-config.topics.image-imu-data")
  val imageObjects: String = ConfigProvider.getEnvString("kafka-config.topics.image-objects")
  val kafkaBootStrapServer: String = ConfigProvider.getEnvString("kafka-config.bootstrap-server")
}
