package edu.knoldus

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try


trait ConfigProvider {
  val conf: Config = ConfigFactory.load()

  def getEnvString(key: String): String = Try(conf.getString(key)).toOption.getOrElse("")

  def getEnvInt(key: String): Int = Try(conf.getInt(key)).toOption.getOrElse(0)

  def getEnvShort(key: String): Short = Try(getEnvInt(key).toShort).toOption.getOrElse(0)
}

object ConfigProvider extends ConfigProvider
