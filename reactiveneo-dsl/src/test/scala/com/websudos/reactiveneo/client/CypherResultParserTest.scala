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

import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.Reads._
import play.api.libs.json._

class CypherResultParserTest extends FlatSpec with Matchers {

  it should "parse successful result" in {
    val reads = (__ \ "name").read[String]
    val parser = new CypherResultParser[String]()(reads)
    val json =
      """
        |{
        |  "results":[{
        |    "columns":["a"],
        |    "data":[
        |      {
        |        "row":["test1"]
        |      },
        |      {
        |        "row":["test2"]
        |      }
        |    ]
        |  }],
        |  "errors":[]
        |}
      """.stripMargin
    val js = Json.parse(json)
    val result = parser.parseResult(js)
    result shouldEqual Seq("test1", "test2")
  }



  it should "parse error result" in {
    val reads = (__ \ "name").read[String]
    val parser = new CypherResultParser[String]()(reads)
    val json =
      """
        |{
        |  "results":[],
        |  "errors":[ {
        |    "code" : "Neo.ClientError.Statement.InvalidSyntax",
        |    "message" : "Invalid input 'T': expected <init> (line 1, column 1)\n\"This is not a valid Cypher Statement.\"\n ^"
        |  } ]
        |}
      """.stripMargin
    val js = Json.parse(json)
    val thrown = intercept[RestClientException] {
      parser.parseResult(js)
    }
    thrown.errors.head.code shouldEqual "Neo.ClientError.Statement.InvalidSyntax"
  }

}
