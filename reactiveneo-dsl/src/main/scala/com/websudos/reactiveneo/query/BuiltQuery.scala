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
package com.websudos.reactiveneo.query

/**
 * Expanded match query that is enriched with every DSL query function call.
 */
case class BuiltQuery(queryString: String) {

  def this() = this("")


  def wrap(str: String,open: String = "(", close: String = ")"): BuiltQuery = pad.append(open).append(str).append(close)
  def wrap(query: BuiltQuery): BuiltQuery = wrap(query.queryString)
  def wrapped(open: String = "(", close: String = ")"): BuiltQuery = BuiltQuery(s"$open$queryString$close")

  def append(str: String): BuiltQuery = new BuiltQuery(queryString + str)
  def append(query: BuiltQuery): BuiltQuery = new BuiltQuery(queryString + query.queryString)
  def append[T](value: T)(implicit formatter: ValueFormatter[T]): BuiltQuery = append(formatter.format(value))

  def appendSpaced(str: String): BuiltQuery = appendSpaced(new BuiltQuery(str))
  def appendSpaced(query: BuiltQuery): BuiltQuery = (if(spaced) this else append(" ")).append(query).append(" ")
  def appendSpaced[T](value: T)(implicit formatter: ValueFormatter[T]): BuiltQuery =
    appendSpaced(new BuiltQuery(formatter.format(value)))

  def space: BuiltQuery = append(" ")

  def spaced: Boolean = queryString.endsWith(" ")
  def pad: BuiltQuery = if (spaced) this else BuiltQuery(queryString + " ")
  def forcePad: BuiltQuery = BuiltQuery(queryString + " ")
  def trim: BuiltQuery = BuiltQuery(queryString.trim)

  override def toString: String = queryString
}

/**
 * Type class used for formatting arbitrary values when building query string.
 * @tparam T Type of value.
 */
trait ValueFormatter[T] {

  def format(value: T): String

}

object DefaultFormatters {

  class IntegerFormatter extends ValueFormatter[Int] {
    override def format(value: Int): String = value.toString
  }

  class DoubleFormatter extends ValueFormatter[Double] {
    override def format(value: Double): String = value.toString
  }

  class BooleanFormatter extends ValueFormatter[Boolean] {
    override def format(value: Boolean): String = if(value) "TRUE" else "FALSE"
  }

  class StringFormatter extends ValueFormatter[String] {
    override def format(value: String): String = s"'$value'"
  }
}