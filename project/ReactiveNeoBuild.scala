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
import BuildSettings._
import Dependencies._
import sbt.Keys._
import sbt._

object ReactiveNeoBuild extends Build {

  lazy val reactiveneo = Project(
    id = "reactiveneo",
    base = file("."),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
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
      Libraries.shapeless,
      Libraries.finagleHttp,
      Libraries.twitterUtilCore,
      Libraries.jodaTime,
      Libraries.jodaConvert,
      Libraries.playJson,
      Libraries.liftJson,
      Libraries.dockerJava
    )
  ).dependsOn(
    reactiveneoTesting % "test, provided"
  )

  lazy val reactiveneoTesting = Project(
    id = "reactiveneo-testing",
    base = file("reactiveneo-testing"),
    settings = Defaults.coreDefaultSettings ++ sharedSettings
  ).settings(
    name := "reactiveneo-testing",
    libraryDependencies ++= Seq(
      Libraries.twitterUtilCore,
      Libraries.datafactory,
      Libraries.finagleHttp,
      Libraries.twitterUtilCore
    )
  )

}
