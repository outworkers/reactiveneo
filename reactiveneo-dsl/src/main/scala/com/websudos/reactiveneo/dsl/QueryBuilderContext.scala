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

/**
 * Context encapsulates available aliases used for query construction.
 */
class QueryBuilderContext {

  private var labels: IndexedSeq[String] = ('a' to 'z').map(_.toString)

  private var aliases: Map[Any, String] = Map()

  /**
   * Return next label value and drops it from the list of available labels.
   */
  def nextLabel: String = {
    val label = labels.head
    labels = labels.tail
    label
  }

  /**
   * Register argument with a next label value and returns next label value and drops it from the list
   * of available labels.
   */
  def nextLabel(go: GraphObject[_,_]): String = {
    val label = labels.head
    labels = labels.tail
    register(go, label)
    label
  }



  /**
   * Register a new alias denoted by a label.
   * @param go Object of the query
   * @param label Associated label
   */
  def register(go: Any, label: String) {
    aliases = aliases + (go -> label)
  }

  /**
   * Resolves label value from registered aliases.
   * @param go Graph object to resolve label for
   * @return Returns label name
   */
  def resolve(go: GraphObject[_,_]): String = {
    aliases(go)
  }

}
