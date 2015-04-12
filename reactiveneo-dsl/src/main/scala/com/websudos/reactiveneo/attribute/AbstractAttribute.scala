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
package com.websudos.reactiveneo.attribute

import com.websudos.reactiveneo.dsl.GraphObject
import com.websudos.reactiveneo.query.QueryRecord

import scala.reflect.runtime.{currentMirror => cm}

/**
 * Attribute definition that can be associated with a node or a relationship
 */
abstract class AbstractAttribute[@specialized(Int, Double, Float, Long, Boolean, Short) T] {

  lazy val name: String = cm.reflect(this).symbol.name.toTypeName.decoded

  /**
   * Decode attribute value from the query result.
   * @param query Query result data.
   * @return Decoded attribute value.
   */
  def apply(query: QueryRecord): Option[T]

}

abstract class Attribute[Owner <: GraphObject[Owner, R], R, T](val owner: GraphObject[Owner, R])
  extends AbstractAttribute[T] {

}


/**
 * String attribute definition.
 */
class StringAttribute[Owner <: GraphObject[Owner, R], R](graphObject: GraphObject[Owner, R])
  extends Attribute[Owner, R, String](graphObject) {

  override def apply(query: QueryRecord): Option[String] = {
    query[String](name)
  }

}

/**
 * Long attribute definition.
 */
class LongAttribute[Owner <: GraphObject[Owner, R], R](graphObject: GraphObject[Owner, R])
  extends Attribute[Owner, R, Long](graphObject) {

  override def apply(query: QueryRecord): Option[Long] = {
    query[Long](name)
  }

}


class IntegerAttribute[Owner <: GraphObject[Owner, R], R](graphObject: GraphObject[Owner, R])
  extends Attribute[Owner, R, Int](graphObject) {

  override def apply(query: QueryRecord): Option[Int] = {
    query[Int](name)
  }

}
