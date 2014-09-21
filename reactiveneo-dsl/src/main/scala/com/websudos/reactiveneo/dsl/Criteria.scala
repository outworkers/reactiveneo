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
import com.websudos.reactiveneo.query.{ValueFormatter, BuiltQuery, CypherOperators}

/**
 * Filtering criteria applied to a selected node.
 * The criteria is referred to a single object type of the graph schema.
 */
abstract class Criteria[Owner <: GraphObject[Owner,_]] (val owner: Owner) {

  /**
   * Cypher clause for this criteria.
   */
  def clause: BuiltQuery

}

class EqualsCriteria[Owner <: GraphObject[Owner,_], T] (owner: Owner, attribute: AbstractAttribute[T], value: T)
                                                       (implicit formatter: ValueFormatter[T])
  extends Criteria[Owner](owner) {

  /**
   * Cypher clause for this criteria.
   */
  override val clause: BuiltQuery = {
    if(value == null)
      throw new IllegalArgumentException("NULL is not allowed value to compare in equals operator. Use `is null` instead.")
    new BuiltQuery(attribute.name).appendSpaced(CypherOperators.EQ).append(value)
  }

}
