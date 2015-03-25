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

import com.websudos.reactiveneo.dsl.{ObjectReturnExpression, TestNode, TestNodeRecord}
import org.scalatest.{Matchers, FlatSpec}
import com.websudos.util.testing._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class RestCallTest extends FlatSpec with Matchers with ServerMockSugar {

  it should "execute call and parse result" in {
    val testNode = new TestNode
    val retEx = new ObjectReturnExpression[TestNode, TestNodeRecord](testNode)
    withServer( req =>
      """
        |{
        |  "results" : [ {
        |    "columns" : [ "tn" ],
        |    "data" : [ {
        |      "row" : [ {"name": "Test name"} ]
        |    } ]
        |  } ],
        |  "errors" : [ ]
        |}
      """.stripMargin, addr => {
        val configuration = ClientConfiguration(addr.getHostName, addr.getPort, 1 second)
        implicit val client = new RestClient(configuration)

        val call = RestCall(SingleTransaction, retEx.resultParser, "match (tn: TestNode) return tn")
        val result = call.execute
        result successful { res =>
          res should have length 1
          res.head.name shouldEqual "Test name"
        }
    })
  }

}
