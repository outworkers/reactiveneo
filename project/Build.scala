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
import org.scoverage.coveralls.CoverallsPlugin.coverallsSettings

import sbt.Keys._
import sbt._
import scoverage.ScoverageSbtPlugin.instrumentSettings
import org.scalastyle.sbt.ScalastylePlugin

object reactiveneo extends Build {

  val scalaMajorVersion = "2.11"
  val scalaMinorVersion = "5"
  val scalaFullVersion = scalaMajorVersion + "." + scalaMinorVersion

  val UtilVersion = "0.5.0"
  val FinagleVersion = "6.24.0"
  val playVersion = "2.3.7"
  val ScalatestVersion = "2.2.1"
  val ScalazVersion = "7.1.0"
  val Neo4jVersion = "2.1.7"

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
            <id>bjankie1</id>
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
    scalaVersion := scalaFullVersion,
    resolvers ++= Seq(
      "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
      "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
      "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
      "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
      "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
      "Twitter Repository"               at "http://maven.twttr.com",
      "Websudos releases"                at "http://maven.websudos.co.uk/ext-release-local",
      "Websudos snapshots"               at "http://maven.websudos.co.uk/ext-snapshot-local"
    ),
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
      "com.chuusai"                  %%  "shapeless"                        % "2.0.0",
      "com.github.nscala-time"       %% "nscala-time"                       % "1.0.0",
      "com.typesafe.scala-logging"   %% "scala-logging-slf4j"               % "2.1.2",
      "com.websudos"                 %% "util-testing"                      % UtilVersion         % "test",
      "org.scalaz"                   %% "scalaz-scalacheck-binding"         % ScalazVersion       % "test",
      "org.scalatest"                %% "scalatest"                         % ScalatestVersion    % "test, provided",
      "org.scalamock"                %% "scalamock-scalatest-support"       % "3.2.1"             % "test"
    ),
    fork in Test := true,
    javaOptions in Test ++= Seq("-Xmx2G")
  ) ++ net.virtualvoid.sbt.graph.Plugin.graphSettings ++ instrumentSettings ++ publishSettings ++ ScalastylePlugin.Settings

  lazy val reactiveneo = Project(
    id = "reactiveneo",
    base = file("."),
    settings = Defaults.coreDefaultSettings ++ sharedSettings ++ coverallsSettings
  ).settings(
    name := "ReactiveNeo"
  ).aggregate(
    reactiveneoDsl,
    reactiveneoTesting
  )

  lazy val reactiveneoDsl = Project(
    id = "reactiveneo-dsl",
    base = file("reactiveneo-dsl"),
    settings = Defaults.coreDefaultSettings ++
      sharedSettings ++
      publishSettings
  ).settings(
    name := "reactiveneo-dsl",
    libraryDependencies ++= Seq(
      "org.scala-lang"               %  "scala-reflect"                     % scalaFullVersion,
      "com.twitter"                  %% "finagle-http"                      % FinagleVersion,
      "com.twitter"                  %% "util-core"                         % "6.23.0",
      "joda-time"                    %  "joda-time"                         % "2.3",
      "org.joda"                     %  "joda-convert"                      % "1.6",
      "com.typesafe.play"            %% "play-json"                         % playVersion,
      "org.neo4j"                    % "neo4j-cypher"                       % Neo4jVersion
    )
  ).dependsOn(
    reactiveneoTesting % "test, provided"
  )

  lazy val reactiveneoTesting = Project(
    id = "reactiveneo-testing",
    base = file("reactiveneo-testing"),
    settings = Defaults.coreDefaultSettings
  ).settings(
    name := "reactiveneo-testing",
    scalaVersion := "2.10.4", //neo4j cypher in the embedded server is using Scala 2.10.x
    libraryDependencies ++= Seq(
      "com.twitter"                      %% "util-core"                % "6.23.0",
      "com.twitter"                      %% "finagle-http"             % FinagleVersion,
      "org.scalatest"                    %% "scalatest"                % ScalatestVersion,
      "org.fluttercode.datafactory"      %  "datafactory"              % "0.8",
      "org.neo4j"                        %  "neo4j-cypher"             % Neo4jVersion % "compile",
      "org.neo4j"                        %  "neo4j-kernel"             % Neo4jVersion % "compile"
        classifier "tests"
    ),
    fork in Test := true,
    javaOptions in Test ++= Seq("-Xmx2G")
  )

}
