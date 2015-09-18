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

sealed trait Direction {
  def symbol: String

  override def toString: String = symbol
}

object Left extends Direction {
  override def symbol: String = "<-"
}

object Right extends Direction {
  override def symbol: String = "->"
}

object Both extends Direction {
  override def symbol: String = "-"
}

/**
 * Used for the first link in the pattern.
 */
object Start extends Direction {
  override def symbol: String = ""
}

/**
 * ADT root for Pattern object.
 */
sealed trait Pattern { self =>

  def queryClause( context: QueryBuilderContext ): String

  /**
   * Iterates all nodes and relationships objects in the pattern.
   * @param action Action to be applied to every single node and relationship
   */
  def foreach( action: GraphObject[_,_] => Unit ): Unit = {
    self match {
      case p: PatternLink[_, _] =>
        action(p.node.owner: GraphObject[_,_])
        p.next.foreach(action)
      case _ => //end of story
    }
  }
}

/**
 * Relation defines path to other node in the query. It defines relationship between the nodes and direction
 * to/from the relationship.
 * n - r - n - r - n
 */
final case class PatternLink[GO <: GraphObject[GO, _], PC <: Pattern]( dir: Direction,
                                                                       node: GraphObjectSelection[GO],
                                                                       next: PC = PNil )
  extends Pattern {

  def queryClause( context: QueryBuilderContext ): String = {
    s"${node.queryClause(context)} $dir ${next.queryClause(context)}"
  }

  //TODO: use phantom type to mark the pattern as closed when it is finished with node or open when finished with a relationship
  def :->:[N <: GraphObject[N, _]]( go: GraphObjectSelection[N] ) = {
    PatternLink(Right, go, this)
  }

  def :<-:[N <: GraphObject[N, _]]( go: GraphObjectSelection[N] ) = {
    PatternLink(Left, go, this)
  }

  def :-:[N <: GraphObject[N, _]]( go: GraphObjectSelection[N] ) = {
    PatternLink(Both, go, this)
  }

}


sealed trait PNil extends Pattern

case object PNil extends PNil {
  override def queryClause( context: QueryBuilderContext ): String = ""
}

object ~~ {

  def unapply[GO <: GraphObject[GO, _], PC <: Pattern]( link: PatternLink[GO, PC] ): Option[(GO, Pattern)] = {
    Some((link.node.owner, link.next))
  }

}
