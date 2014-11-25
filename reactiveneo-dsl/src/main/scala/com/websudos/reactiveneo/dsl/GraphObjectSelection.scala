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

import com.websudos.reactiveneo.attribute.AbstractAttribute
import com.websudos.reactiveneo.query.{ValueFormatter, CypherOperators, BuiltQuery}

/**
 * Criteria applied to a graph object. Cypher representation of a pattern is ```(n: Label { name = "Mark" })```
 */
private[reactiveneo] case class GraphObjectSelection[Owner <: GraphObject[Owner, _]](
   owner: Owner,
   predicates: Predicate[_]*) {

  @inline
  private def predicatesQuery: Option[BuiltQuery] = {
    if(predicates.nonEmpty) {
      Some(predicates.tail.foldLeft(predicates.head.clause)((agg, next) => agg.append(",").append(next.clause)))
    } else {
      None
    }
  }

  /**
   * Builds a query string of alias, object name and criteria if some.
   */
  def queryClause(context: QueryBuilderContext): BuiltQuery = {
    val alias = context.resolve(owner)
    val (open:String, close:String) = this.owner match {
      case _:Relationship[_,_] => "[" -> "]"
      case _                   => "(" -> ")"
    }
    BuiltQuery(s"$alias:${owner.objectName}")
      .append(predicatesQuery.map(" {" + _.queryString + "}").getOrElse(""))
      .wrapped(open, close)
  }

  override def toString: String = {
    owner.objectName + predicatesQuery.map(" {" + _.queryString + "}")
  }
}

/**
 * Predicate filtering nodes by attribute value.
 * @param attribute Attribute to filter.
 * @param value Lookup value
 */
private[reactiveneo] case class Predicate[T](
  attribute: AbstractAttribute[T], value: T)(implicit formatter: ValueFormatter[T]) {

  val clause: BuiltQuery = {
    if(value == null)
      throw new IllegalArgumentException("NULL value is not allowed in predicate.")
    new BuiltQuery(attribute.name).append(CypherOperators.COLON).append(value)
  }

}

trait PredicateOps {

  implicit class PredicateFunctions[V](attr: AbstractAttribute[V])(implicit formatter: ValueFormatter[V]) {

    implicit def :=(value: V): Predicate[V] = {
      new Predicate[V](attr, value)
    }

  }
}