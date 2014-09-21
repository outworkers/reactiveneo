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

import com.websudos.reactiveneo.attribute.Attribute
import com.websudos.reactiveneo.client.ResultParser
import com.websudos.reactiveneo.query.{BuiltQuery, CypherOperators}

/**
 * Return expression defines the element of nodes that need to be returned. It can be the whole node
 * or particular attributes.
 * @tparam R Returned type - it can be a type of graph object or it's attribute
 */
abstract class ReturnExpression[R] {

  /**
   * Builds part of the query string corresponding to this expression.
   * @return Returns the built query.
   */
  def query(aliases: Map[GraphObject[_, _], String]): BuiltQuery

  /**
   * Builds result parser for this expression.
   * @return Returns the result parser.
   */
  def buildParser: ResultParser[R]

}

/**
 * Expression that defines return value being a graph object.
 * @param go Graph object being returned.
 * @tparam GO Class of the graph object definition
 * @tparam R Returned type - graph object record type in this case
 */
case class ObjectReturnExpression[GO <: GraphObject[GO, R], R](go: GraphObject[GO, R]) extends ReturnExpression[R] {


  override def query(aliases: Map[GraphObject[_, _], String]): BuiltQuery = {
    aliases(go)
  }


  override def buildParser: ResultParser[R] = ???
}


/**
 * Expression that defines return data being an attribute of a graph object.
 * @param attribute Attribute type to be returned.
 * @tparam R Returned type - an attribute concrete type in this case.
 */
case class AttributeReturnExpression[GO <: GraphObject[GO, R], R, T](
    attribute: Attribute[GO, R, T]) extends ReturnExpression[T] {


  override def query(aliases: Map[GraphObject[_, _], String]): BuiltQuery = {
    aliases(attribute.owner.asInstanceOf[GraphObject[_,_]]) + CypherOperators.DOT + attribute.name
  }


  override def buildParser: ResultParser[T] = ???
}

/**
 * Implicits converting [[com.websudos.reactiveneo.dsl.GraphObject]] to [[com.websudos.reactiveneo.dsl.ObjectReturnExpression]]
 * and [[com.websudos.reactiveneo.attribute.AbstractAttribute]] to [[com.websudos.reactiveneo.dsl.AttributeReturnExpression]]
 */
trait ReturnImplicits {

 implicit def attributeToReturnExpression[GO <: GraphObject[GO, R], R, T](attr: Attribute[GO, R, T]): AttributeReturnExpression[GO, R, T] = {
   AttributeReturnExpression(attr)
 }

  implicit def graphObjectToReturnExpression[GO <: GraphObject[GO, R], R](go: GraphObject[GO, R]): ObjectReturnExpression[GO, R] = {
    ObjectReturnExpression(go)
  }

}