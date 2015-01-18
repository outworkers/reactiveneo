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

import com.typesafe.scalalogging.slf4j.LazyLogging
import com.websudos.reactiveneo.dsl.MatchQuery
import org.neo4j.cypher.javacompat.ExecutionEngine
import org.neo4j.graphdb.GraphDatabaseService

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
 * Embedded call definition
 */
class EmbeddedCall[RT](query: String)
                      (implicit graphDb: GraphDatabaseService)
  extends ServerCall[Seq[RT]]
  with LazyLogging {


  override def execute( ): Future[Seq[RT]] = {
    val engine = new ExecutionEngine(graphDb)
    try {
      val tx = graphDb.beginTx()
      val result = engine.execute(query)
      Future.successful(result.iterator().asScala.map(_.values().asScala.head.asInstanceOf[RT]).toSeq)
    } catch {
      case ex: Exception =>
        logger.error("Failed to execute call")
        Future.failed(ex)
    }
  }

}


/**
 * Service that prepares and executes rest call
 */
trait EmbeddedCallService {

  implicit def client: GraphDatabaseService

  implicit def makeRequest[RT <: MatchQuery[_,_,_,_,_,RT]](matchQuery: MatchQuery[_,_,_,_,_,RT]): EmbeddedCall[RT] = {
    val (query, retType) = matchQuery.finalQuery
    val call = new EmbeddedCall[RT](query)
    call
  }

}