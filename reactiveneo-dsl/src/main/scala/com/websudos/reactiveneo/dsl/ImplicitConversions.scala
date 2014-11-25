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

import com.websudos.reactiveneo.query.BuiltQuery

/**
 * Commonly used implicit conversions
 */
trait ImplicitConversions {

  /**
   * Wraps a string with a BuiltQuery object.
   * @param str String to wrap
   * @return Returns query object.
   */
  implicit def stringToQuery(str: String): BuiltQuery = BuiltQuery(str)

  /**
   * Conversion that simplifies query building. It allows to build the query directly from a pattern.
   * ```
   * PersonNode(p=>p.name := "Mark").returns(p=>p)
   * ```
   * @param p Predicate that forms initial node for the query
   * @tparam P Pattern type.
   * @return Returns query object.
   */
  implicit def patternToQuery[P <: Pattern](p: P): MatchQuery[P, WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound, _] = {
    MatchQuery.createRootQuery(p, new QueryBuilderContext)
  }


  /**
   * Convert single node selection to the [[com.websudos.reactiveneo.dsl.Pattern]] object
   * @param sel Graph node selection
   * @tparam N Type of node
   * @return Returns Pattern with given [[com.websudos.reactiveneo.dsl.GraphObjectSelection]] as root.
   */
  implicit def selectionToPattern[N <: Node[N,_]](sel: GraphObjectSelection[N]): PatternLink[N, PNil] = {
    val pattern = new PatternLink[N, PNil](Start, sel)
    pattern
  }

  /**
   * Convert single node selection to the [[com.websudos.reactiveneo.dsl.MatchQuery]] object
   * @param sel Graph node selection
   * @tparam N Type of node
   * @return Returns Query object.
   */
  implicit def selectionToQuery[N <: Node[N,_]](sel: GraphObjectSelection[N]):
  MatchQuery[PatternLink[N,PNil], WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound, _] = {
    val pattern = new PatternLink[N, PNil](Start, sel)
    val query = MatchQuery.createRootQuery(pattern, new QueryBuilderContext)
    query
  }
}
