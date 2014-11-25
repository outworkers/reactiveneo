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

import com.websudos.reactiveneo.client.{ServerCall, SingleTransaction, RestClient}
import com.websudos.reactiveneo.query.{BuiltQuery, CypherKeywords, CypherQueryBuilder}

import scala.annotation.implicitNotFound
import scala.concurrent.Future

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
 * @param pattern Pattern this query is build against.
 * @param builtQuery Current query string.
 * @param context Map of added node types to corresponding alias value used in RETURN clause.
 */
private[reactiveneo] class MatchQuery[
  P  <: Pattern,
  WB <: WhereBind,
  RB <: ReturnBind,
  OB <: OrderBind,
  LB <: LimitBind,
  RT](pattern: P,
      builtQuery: BuiltQuery,
      context: QueryBuilderContext,
      ret: Option[ReturnExpression[RT]] = None) extends CypherQueryBuilder {


  def query: String = builtQuery.queryString


  @implicitNotFound("You cannot use two where clauses on a single query")
  final def where(condition: P => Criteria[_])
                 (implicit ev: WB =:= WhereUnbound): MatchQuery[P, WhereBound, RB, OB, LB, _] = {
    new MatchQuery[P, WhereBound, RB, OB, LB, Any] (
      pattern,
      where(builtQuery, condition(pattern).clause),
      context)
  }

  final def returns[URT](ret: P => ReturnExpression[URT]): MatchQuery[P, WB, ReturnBound, OB, LB, URT]  = {
    new MatchQuery[P, WB, ReturnBound, OB, LB, URT] (
      pattern,
      builtQuery.appendSpaced(CypherKeywords.RETURN).appendSpaced(ret(pattern).query(context)),
      context,
      Some(ret(pattern)))
  }

  @implicitNotFound("You need to add return clause to capture the type of result")
  final def execute(implicit client: RestClient, ev: RB =:= ReturnBound): Future[Seq[RT]] = {
    val call = ServerCall(SingleTransaction, ret.get, builtQuery.queryString)
    call.execute
  }

}

private[reactiveneo] object MatchQuery {


  def createRootQuery[P <: Pattern](
                          pattern: P,
                          context: QueryBuilderContext):
                            MatchQuery[P, WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound, _] = {
    pattern.foreach(context.nextLabel(_))
    val query = new BuiltQuery(CypherKeywords.MATCH).appendSpaced(pattern.queryClause(context))
    new MatchQuery[P, WhereUnbound, ReturnUnbound, OrderUnbound, LimitUnbound, Any](
      pattern,
      query,
      context)
  }
}
