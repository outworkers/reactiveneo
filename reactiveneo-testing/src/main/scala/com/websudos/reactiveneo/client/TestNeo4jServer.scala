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

import java.nio.charset.Charset

import com.twitter.io.Charsets._
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.neo4j.cypher.ExecutionEngine
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.test.TestGraphDatabaseFactory
import org.scalatest.Suite

import scala.util.Try

/**
 * A base for tests that require embedded Neo4j. It leverages [[com.websudos.reactiveneo.client.ServerMock]]
 * to handle http requests.
 */
trait TestNeo4jServer extends Suite {

  var db: GraphDatabaseService = _

  var server: ServerMock = _

  lazy val port: Int = server.port

  override protected def withFixture(test: NoArgTest) = {
    db = new TestGraphDatabaseFactory().newImpermanentDatabase()
    val engine = new ExecutionEngine(db)

    server = new ServerMock(req => {
      val query = req.getContent.toString(Charset.defaultCharset())
      val tx = db.beginTx()
      val result = engine.execute(query)
      tx.success()
      val response = new DefaultHttpResponse(HTTP_1_1, OK)
      response.setContent(copiedBuffer(result.dumpToString(), Utf8))
      response
    })
    try super.withFixture(test) finally db.shutdown()
  }

}
