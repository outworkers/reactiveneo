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
package com.websudos.reactiveneo.dsl

import org.scalatest.{FlatSpec, Matchers}

class MatchQueryTest extends FlatSpec with Matchers {

  implicit val context: QueryBuilderContext = new QueryBuilderContext

  it should "build a simple query with a predicate" in {
    TestNode(_.name := "Tom").returns { case n ~~ _ => n}.query shouldEqual
      "MATCH (a:TestNode {name:'Tom'})   RETURN a "
  }

  it should "build a simple query with a predicate using conversion" in {
    TestNode( _.name := "Tom" ).returns { case go ~~ _ => go}.query shouldEqual
      "MATCH (a:TestNode {name:'Tom'})   RETURN a "
  }


  it should "build a simple query without any predicate" in {
    TestNode().returns{ case go ~~ _ => go}.query shouldEqual "MATCH (a:TestNode)   RETURN a "
  }

  it should "build a query returning object attribute" in {
    TestNode().returns{ case go ~~ _ => go.name}.query shouldEqual "MATCH (a:TestNode)   RETURN a.name "
  }


  it should "build a complex pattern query with a predicate using conversion" in {
    (TestNode( _.name := "Tom" ) :->: TestRelationship() :<-: TestNode()) .returns { case go ~~ _ => go}.query shouldEqual
      "MATCH (a:TestNode {name:'Tom'}) -> [b:TestRelationship] <- (c:TestNode)   RETURN a "
  }

}