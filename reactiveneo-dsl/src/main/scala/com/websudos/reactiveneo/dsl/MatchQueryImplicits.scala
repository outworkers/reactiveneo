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
 * Implicits covering Match query inception.
 */
trait MatchQueryImplicits {

  val nodeAliases: IndexedSeq[String] = ('a' to 'z').map(_.toString)

  /**
   * Initiates match query with result of criteria function. The function takes a function  as an argument
   * that produces Criteria instance.
   * @param predBuilder predicate building function.
   * @return Returns query object
   */
  def matches[N <: Node[N, _]](predBuilder: (N => Predicate[_])*)
                              (implicit m: Manifest[N]): MatchQuery[N, WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound] = {
    val obj = m.runtimeClass.newInstance().asInstanceOf[N]

    val pattern = Pattern(obj, nodeAliases.head, predBuilder.map(pred => pred(obj)): _*)
    MatchQuery.createRootQuery(pattern, nodeAliases.tail)
  }

}
