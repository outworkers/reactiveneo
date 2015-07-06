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
package com.websudos.reactiveneo.client

import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.slf4j.LazyLogging
import com.websudos.reactiveneo.dsl.MatchQuery
import org.jboss.netty.handler.codec.http.HttpMethod
import play.api.libs.json.Reads

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
 * REST API endpoints definitions.
 * @param path Server query path.
 * @param method HTTP method, with POST as default.
 */
case class RestEndpoint(path: String, method: HttpMethod = HttpMethod.POST)
object SingleTransaction extends RestEndpoint("/db/data/transaction/commit")
object BeginTransaction extends RestEndpoint("/db/data/transaction")
class ContinueInTransaction(transactionId: Int) extends RestEndpoint(s"/db/data/transaction/$transactionId")
class CommitTransaction(transactionId: Int) extends RestEndpoint(s"/db/data/transaction/$transactionId/commit")
class RollbackTransaction(transactionId: Int) extends RestEndpoint(s"/db/data/transaction/$transactionId", HttpMethod.DELETE)

/**
 * Model of a call to Neo4j server.
 * @tparam RT Type of result call response.
 */
class RestCall[RT](endpoint: RestEndpoint, content: Option[String], resultParser: Reads[RT])(implicit client: RestClient)
    extends ServerCall[Seq[RT]]
    with LazyLogging {

  implicit lazy val parser = {
    val parser = new CypherResultParser[RT]()(resultParser)
    parser
  }

  def execute: Future[Seq[RT]] = {
    val result = client.makeRequest[Seq[RT]](endpoint.path, endpoint.method, content)
    result
  }

}

object RestCall {

  def apply[RT](endpoint: RestEndpoint, resultParser: Reads[RT], query: String)(implicit client: RestClient) = {
    new RestCall[RT](endpoint, Some(query), resultParser)
  }

  def apply[RT](endpoint: RestEndpoint, resultParser: Reads[RT])(implicit client: RestClient) = {
    new RestCall[RT](endpoint, None, resultParser)
  }
}

/**
 * Service that prepares and executes rest call
 */
class RestConnection(config: ClientConfiguration) {

  implicit def client: RestClient = new RestClient(config)

  def neoStatement( cypher: String ) =
    s"""{
        |  "statements" : [ {
        |    "statement" : "$cypher"
        |  } ]
        |}""".stripMargin

  implicit def makeRequest[RT](matchQuery: MatchQuery[_, _, _, _, _, RT]): RestCall[RT] = {
    val (query, retType) = matchQuery.finalQuery
    val requestContent = neoStatement(query)
    val call = RestCall(SingleTransaction, retType.resultParser, requestContent)
    call
  }


  implicit def makeRequest[RT](cypher: String)(implicit resultParser: Reads[RT]): RestCall[RT] = {
    val requestContent = neoStatement(cypher)
    val call = RestCall(SingleTransaction, resultParser, requestContent)
    call
  }

}

object RestConnection {

  def apply(host: String, port: Int): RestConnection = {
    val config = ClientConfiguration(host, port, FiniteDuration(10, TimeUnit.SECONDS))
    new RestConnection(config)
  }

}