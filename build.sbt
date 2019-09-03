name := "kerb-kafka-publisher"

version := "0.1"

scalaVersion := "2.12.0"

val kafka          = "org.apache.kafka"             % "kafka-clients"       % "2.3.0"
val liftWeb        = "net.liftweb"                  %% "lift-json"          % "3.2.0-M3"
val typeSafeConfig = "com.typesafe"                 % "config"              % "1.3.4"
val hadoopCommon   = "org.apache.hadoop"            % "hadoop-common"       % "3.2.0"
val hadoopClient   = "org.apache.hadoop"            % "hadoop-client"       % "3.2.0"

libraryDependencies ++= Seq(kafka, liftWeb, typeSafeConfig, hadoopCommon, hadoopClient)

