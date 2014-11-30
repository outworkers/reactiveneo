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


class GraphObjectSelectionTest extends FlatSpec with Matchers {

  it should "serialize simple pattern to a string" in {
    val context = new QueryBuilderContext
    val node = new TestNode
    val label = context.nextLabel(node)
    GraphObjectSelection(node).queryClause(context).queryString shouldEqual s"($label:TestNode)"
  }


  it should "serialize pattern with criteria to a string" in {
    val context = new QueryBuilderContext
    val owner = new TestNode
    val label = context.nextLabel(owner)
    GraphObjectSelection(owner, Predicate(owner.name, "Tom")).queryClause(context).queryString shouldEqual s"""($label:TestNode {name:'Tom'})"""
  }
}


