/*
 * Copyright 2014 websudos ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import sbt.Keys._
import sbt._


object Dependencies {
  val resolutionRepos = Seq(
    "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
    "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
    "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
    "Twitter Repository"               at "http://maven.twttr.com",
    Resolver.bintrayRepo("websudos", "oss-releases")
  )


  object V {
    val scalatestVersion = "2.2.0-M1"
    val finagleVersion = "6.27.0"
    val playVersion = "2.3.4"
    val scalazVersion = "7.1.4"
    val shapeless = "2.2.5"
    val jodaTime = "2.8.2"
    val jodaConvert = "1.7"
    val nscalaTime = "2.2.0"
    val scalaLogging = "2.1.2"
    val scalaMock = "3.2.2"
    val datafactory = "0.8"
  }

  object Libraries {
    val shapeless          = "com.chuusai"                  %% "shapeless"                    % V.shapeless
    val finagleHttp        = "com.twitter"                  %% "finagle-http"                 % V.finagleVersion
    val twitterUtilCore    = "com.twitter"                  %% "util-core"                    % V.finagleVersion
    val jodaTime           = "joda-time"                     % "joda-time"                    % V.jodaTime
    val jodaConvert        = "org.joda"                      % "joda-convert"                 % V.jodaConvert
    val playJson           = "com.typesafe.play"            %% "play-json"                    % V.playVersion
    val nscalaTime         = "com.github.nscala-time"       %% "nscala-time"                  % V.nscalaTime
    val scalaLoggingSlf4j  = "com.typesafe.scala-logging"   %% "scala-logging-slf4j"          % V.scalaLogging
    val datafactory        = "org.fluttercode.datafactory"   %  "datafactory"                 % V.datafactory
    val scalaz             = "org.scalaz"                   %% "scalaz-scalacheck-binding"    % V.scalazVersion       % "test"
    val liftJson           = "net.liftweb"                  %% "lift-json"                    % "2.6-M4"             % "test, provided"
    val dockerJava         = "com.github.docker-java"        % "docker-java"                  % "2.1.1"              % "test"
    val scalaTest          = "org.scalatest"                %% "scalatest"                    % V.scalatestVersion    % "test, provided"
    val scalaMock          = "org.scalamock"                %% "scalamock-scalatest-support"  % V.scalaMock             % "test"
  }

}