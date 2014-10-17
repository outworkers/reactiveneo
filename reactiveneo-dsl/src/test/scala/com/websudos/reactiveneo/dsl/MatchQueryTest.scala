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

import org.scalatest.{Matchers, FlatSpec}
import Predicate._

class MatchQueryTest extends FlatSpec with Matchers with ReturnImplicits {

  it should "build a simple query with a predicate" in {
    matches[TestNode]( node => node.name := "Tom" ).returns(go => go).query shouldEqual "MATCH (a:TestNode {name:'Tom'}) RETURN a "
  }

  it should "build a simple query without any predicate" in {
    matches[TestNode]().returns(n => n).query shouldEqual "MATCH (a:TestNode) RETURN a "
  }

  it should "build a query returning single object" in {
    matches[TestNode]().returns(n => n).query shouldEqual "MATCH (a:TestNode) RETURN a "
  }

  it should "build a query returning object attribute" in {
    matches[TestNode]().returns(_.name).query shouldEqual "MATCH (a:TestNode) RETURN a.name "
  }
}