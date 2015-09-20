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
import Dependencies._
import com.twitter.sbt.VersionManagement
import com.twitter.sbt.VersionManagement
import org.scalastyle.sbt.ScalastylePlugin
import sbt._
import Keys._
import scoverage.ScoverageSbtPlugin

object BuildSettings {

  val publishUrl = "http://maven.websudos.co.uk"

  val mavenPublishSettings : Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true,
    publishTo <<= version.apply {
      v =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true },
    pomExtra :=
      <url>https://github.com/websudosuk/reactiveneo</url>
        <licenses>
          <license>
            <name>Apache License, Version 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:websudosuk/reactiveneo.git</url>
          <connection>scm:git:git@github.com:websudosuk/reactiveneo.git</connection>
        </scm>
        <developers>
          <developer>
            <id>benjumanji</id>
            <name>Bartosz Jankiewicz</name>
            <url>http://github.com/bjankie1</url>
          </developer>
          <developer>
            <id>alexflav</id>
            <name>Flavian Alexandru</name>
            <url>http://github.com/alexflav23</url>
          </developer>
        </developers>
  )

  val publishSettings : Seq[Def.Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishTo <<= version { (v: String) => {
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at publishUrl + "/ext-snapshot-local")
      else
        Some("releases"  at publishUrl + "/ext-release-local")
    }
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => true }
  )

  val sharedSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.websudos",
    version := "0.1.2",
    scalaVersion := "2.11.7",
    resolvers ++= resolutionRepos,
    scalacOptions ++= Seq(
      "-language:postfixOps",
      "-language:implicitConversions",
      "-language:reflectiveCalls",
      "-language:higherKinds",
      "-language:existentials",
      "-Yinline-warnings",
      "-Xlint",
      "-deprecation",
      "-feature",
      "-unchecked"
    ),
    libraryDependencies ++= Seq(
      Libraries.shapeless,
      Libraries.nscalaTime,
      Libraries.scalaLoggingSlf4j,
      Libraries.scalaz,
      Libraries.scalaTest,
      Libraries.scalaMock
    ),
    fork in Test := true,
    javaOptions in Test ++= Seq("-Xmx2G")
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ publishSettings ++ VersionManagement.newSettings

}