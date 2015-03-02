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
package com.websudos.reactiveneo.client

import com.websudos.reactiveneo.dsl._
import com.websudos.util.testing._
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._
import org.scalatest.{ FeatureSpec, GivenWhenThen, Matchers }
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global

class RestClientSpec extends FeatureSpec with GivenWhenThen with Matchers {

  info("As a user")
  info("I want to be able to make a call to Neo4j server")
  info("So I can get the data")
  info("And never worry about REST result parsing")

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  feature("REST client") {

    scenario("send a simple MATCH query") {
      Given("started Neo4j server")
      implicit val service = RestConnection("localhost", 7474)
      val query: MatchQuery[_, _, _, _, _, TestNodeRecord] = TestNode().returns { case go ~~ _ => go }

      When("REST call is executed")
      val result = query.execute

      Then("The result should be delivered")
      result successful { res =>
        res should not be empty
      }

    }

    scenario("send a custom query and use a naive parser") {
      Given("started Neo4j server")
      val service = RestConnection("localhost", 7474)
      val query = "MATCH (n:TestNode) RETURN n"
      implicit val parser: Reads[Int] = __.read[JsObject].map(_ => 6)

      When("REST call is executed")
      val result = service.makeRequest[Int](query).execute

      Then("The result should be delivered")
      result successful { res =>
        res should not be empty
        res should contain only 6
      }

    }

  }

}
