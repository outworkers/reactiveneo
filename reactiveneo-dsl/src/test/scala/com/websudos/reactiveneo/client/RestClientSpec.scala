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

import com.websudos.reactiveneo.RequiresNeo4jServer
import com.websudos.reactiveneo.dsl._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class InsertResult(id: Int)

case class Person(name: String, age: Int)

class RestClientSpec extends FeatureSpec with GivenWhenThen with Matchers
    with ScalaFutures with IntegrationPatience {

  info("As a user")
  info("I want to be able to make a call to Neo4j server")
  info("So I can get the data")
  info("And expect the the result to be parsed for me")

  feature("REST client") {

    scenario("send a simple MATCH query", RequiresNeo4jServer) {
      Given("started Neo4j server")
      implicit val service = RestConnection("localhost", 7474)
      val query: MatchQuery[_, _, _, _, _, TestNodeRecord] = TestNode().returns { case go ~~ _ => go }

      When("REST call is executed")
      val result = query.execute

      Then("The result should be delivered")
      whenReady(result) { res =>
        res should not be empty
      }

    }

    scenario("send a query and use a custom parser to get the result", RequiresNeo4jServer) {
      Given("started Neo4j server")
      val service = RestConnection("localhost", 7474)
      val query = "CREATE (n) RETURN id(n)"
      implicit val parser: Reads[InsertResult] = __.read[Int].map { arr =>
        InsertResult(arr)
      }

      When("REST call is executed")
      val result = service.makeRequest[InsertResult](query).execute

      Then("The result should be delivered")
      whenReady(result) { res =>
        res should not be empty
        res.head.id shouldBe >(0)
      }
    }

    scenario("create a Person node and load it", RequiresNeo4jServer) {
      Given("started Neo4j server")
      val service = RestConnection("localhost", 7474)
      val query = "CREATE (p: Person { name: 'Mike', age: 10 }) RETURN p"
      implicit val parser: Reads[Person] = ((__ \ "name").read[String] and (__ \ "age").read[Int])(Person)

      When("REST call is executed")
      val result = service.makeRequest[Person](query).execute

      Then("The result should be delivered")
      whenReady(result) { res =>
        res should not be empty
        res.head.name shouldBe "Mike"
      }
    }

  }

}
