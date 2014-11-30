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

class PatternTest extends FlatSpec with Matchers {


  it should "serialize a simple pattern" in {
    implicit val context:QueryBuilderContext = new QueryBuilderContext
    val node = TestNode()
    val relation = PatternLink(Start, node)
    val nodeLabel = context.nextLabel(node.owner)
    val cypherPart = relation.queryClause(context)
    cypherPart.trim shouldEqual s"($nodeLabel:TestNode)"
  }


  it should "serialize a pattern with direction functions" in {
    implicit val context:QueryBuilderContext = new QueryBuilderContext
    val node = TestNode()
    val rel = TestRelationship()
    val node1 = TestNode( tn => Predicate(tn.name, "Mark"))
    val pattern = node :->: rel :<-: node1
    val nodeLabel = context.nextLabel(node.owner)
    val node1Label = context.nextLabel(node1.owner)
    val relLabel = context.nextLabel(rel.owner)
    val cypherPart = pattern.queryClause(context)
    cypherPart.trim shouldEqual
      s"($nodeLabel:TestNode) -> [$relLabel:TestRelationship] <- ($node1Label:TestNode {name:'Mark'})"
  }

  it should "serialize a pattern with direction functions even more elaborate example" in {
    implicit val context:QueryBuilderContext = new QueryBuilderContext
    val node1 = TestNode()
    val rel1 = TestRelationship()
    val node2 = TestNode( _.name := "Mark")
    val rel2 = TestRelationship(_.year := 2000)
    val node3 = TestNode(_.name := "Jane")
    val pattern = node1 :->: rel1 :<-: node2 :-: rel2 :->: node3
    val node1Label = context.nextLabel(node1.owner)
    val rel1Label = context.nextLabel(rel1.owner)
    val node2Label = context.nextLabel(node2.owner)
    val rel2Label = context.nextLabel(rel2.owner)
    val node3Label = context.nextLabel(node3.owner)
    val cypherPart = pattern.queryClause(context)
    cypherPart.trim shouldEqual
      s"($node1Label:TestNode) -> [$rel1Label:TestRelationship] <- ($node2Label:TestNode {name:'Mark'}) " +
        s"- [$rel2Label:TestRelationship {year:2000}] -> ($node3Label:TestNode {name:'Jane'})"
  }


}
