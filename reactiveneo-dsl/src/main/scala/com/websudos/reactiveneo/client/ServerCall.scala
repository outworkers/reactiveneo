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

import com.websudos.reactiveneo.dsl.ReturnExpression
import org.jboss.netty.handler.codec.http.HttpMethod

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
class ServerCall[RT](endpoint: RestEndpoint, content: Option[String], returnExpression: ReturnExpression[RT])
(implicit client: RestClient) {

  implicit lazy val parser = {
    val parser = new CypherResultParser[RT]()(returnExpression.resultParser)
    parser
  }


  def execute = {
    val result = client.makeRequest[Seq[RT]](endpoint.path, endpoint.method)
    result
  }

}

object ServerCall {

  def apply[RT](endpoint: RestEndpoint, returnExpression: ReturnExpression[RT], query: String)(implicit client: RestClient) = {
    new ServerCall[RT](endpoint, Some(query), returnExpression)
  }

  def apply[RT](endpoint: RestEndpoint, returnExpression: ReturnExpression[RT])(implicit client: RestClient) = {
    new ServerCall[RT](endpoint, None, returnExpression)
  }
}