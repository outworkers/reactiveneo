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

import com.websudos.reactiveneo.attribute.StringAttribute
import com.websudos.reactiveneo.query.QueryRecord

case class TestNodeRecord(name: String)


class TestNode extends Node[TestNode, TestNodeRecord] {

  object name extends StringAttribute(this)

  override def fromQuery(data: QueryRecord): TestNodeRecord = {
    TestNodeRecord(name(data).getOrElse(""))
  }
}

object TestNode extends TestNode