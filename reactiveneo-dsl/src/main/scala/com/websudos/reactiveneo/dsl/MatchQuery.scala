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

import com.websudos.reactiveneo.query.{CypherOperators, BuiltQuery, CypherKeywords, CypherQueryBuilder}

import scala.annotation.implicitNotFound

sealed trait RelationshipDirection

private[reactiveneo] abstract class Any extends RelationshipDirection

private[reactiveneo] abstract class Left extends RelationshipDirection

private[reactiveneo] abstract class Right extends RelationshipDirection

sealed trait RelationshipBind

private[reactiveneo] abstract class RelationshipBound extends RelationshipBind

private[reactiveneo] abstract class RelationshipUnbound extends RelationshipBind

sealed trait WhereBind

private[reactiveneo] abstract class WhereBound extends WhereBind

private[reactiveneo] abstract class WhereUnbound extends WhereBind

sealed trait ReturnBind

private[reactiveneo] abstract class ReturnBound extends ReturnBind

private[reactiveneo] abstract class ReturnUnbound extends ReturnBind

sealed trait OrderBind

private[reactiveneo] abstract class OrderBound extends OrderBind

private[reactiveneo] abstract class OrderUnbound extends OrderBind

sealed trait LimitBind

private[reactiveneo] abstract class LimitBound extends LimitBind

private[reactiveneo] abstract class LimitUnbound extends LimitBind

/**
 * Query builder is responsible for encapsulating nodes information and selection criteria.
 * @param node Initial node to query against.
 * @param builtQuery Current query string.
 * @param aliases Map of added node types to corresponding alias value used in RETURN clause.
 */
private[reactiveneo] class MatchQuery[
  GO <: Node[GO, _],
  WB <: WhereBind,
  RB <: ReturnBind,
  OB <: OrderBind,
  LB <: LimitBind ](node: GO, builtQuery: BuiltQuery, aliases: Map[GraphObject[_, _], String]) extends CypherQueryBuilder {


  def query: String = builtQuery.queryString


  @implicitNotFound("You cannot use two where clauses on a single query")
  final def where(condition: GO => Criteria[GO]): MatchQuery[GO, WhereBound, RB, OB, LB] = {
    new MatchQuery[GO, WhereBound, RB, OB, LB](
      node,
      where(builtQuery, condition(node).clause),
      aliases)
  }

  final def returnAll: MatchQuery[GO, WB, ReturnBound, OB, LB]  = {
    new MatchQuery[GO, WB, ReturnBound, OB, LB](
      node,
      builtQuery.appendSpaced(CypherKeywords.RETURN).appendSpaced(CypherOperators.WILDCARD),
      aliases)
  }

  final def returns(): MatchQuery[GO, WB, ReturnBound, OB, LB]  = {
    new MatchQuery[GO, WB, ReturnBound, OB, LB](
      node,
      builtQuery.appendSpaced(CypherKeywords.RETURN).appendSpaced(aliases.values.mkString(",")),
      aliases)
  }


  final def returns(ret: GO => ReturnExpression[_]): MatchQuery[GO, WB, ReturnBound, OB, LB]  = {
    new MatchQuery[GO, WB, ReturnBound, OB, LB](
      node,
      builtQuery.appendSpaced(CypherKeywords.RETURN).appendSpaced(aliases.values.mkString(",")),
      aliases)
  }

}

private[reactiveneo] object MatchQuery {


  def createRootQuery[GO <: Node[GO, _]](pattern: Pattern[GO], aliases: IndexedSeq[String]): MatchQuery[GO, WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound] = {
    val query = new BuiltQuery(CypherKeywords.MATCH).appendSpaced(pattern.clause)
    new MatchQuery[GO, WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound](
      pattern.owner,
      query,
      Map(pattern.owner -> pattern.alias))
  }
}
