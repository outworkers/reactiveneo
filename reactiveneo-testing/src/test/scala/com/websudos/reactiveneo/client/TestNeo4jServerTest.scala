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

import com.twitter.finagle.Http
import com.twitter.util
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpResponse, HttpVersion, DefaultHttpRequest}
import org.scalatest.FlatSpec

class TestNeo4jServerTest extends FlatSpec with TestNeo4jServer with LazyLogging {

  //TODO: embedded Neo4j uses different Scala version in cypher library failing this test
  ignore should "pass a query to embedded server" in {
    val path = s"http://localhost:$port/db/data/transaction/commit"
    val query = """{
                    |  "statements" : [ {
                    |    "statement" : "CREATE (n) RETURN id(n)"
                    |  } ]
                    |}""".stripMargin
    val request =  new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path)
    request.setContent(ChannelBuffers.copiedBuffer(query, Charset.forName("UTF-8")))

    val client = Http.newService(s"localhost:$port")
    val response: util.Future[HttpResponse] = client(request)
    response onSuccess { resp: HttpResponse =>
      logger.debug("GET success: " + resp)
    }

  }

}
