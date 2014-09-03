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
package com.websudos.neo.client

import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

import com.newzly.util.testing.AsyncAssertionsHelper._
import com.twitter.finagle.Service
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.Http
import com.twitter.io.Charsets.Utf8
import com.twitter.util.Future
import com.websudos.neo.client.RestClient._
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http._
import org.scalatest._
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

import scala.concurrent.duration.FiniteDuration

class RestClientTest extends FlatSpec with Matchers with BeforeAndAfter {

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  var server: Server = _

  def startServer: Server = {
    class Respond extends Service[HttpRequest, HttpResponse] {
      def apply(request: HttpRequest) = {
        val response = new DefaultHttpResponse(HTTP_1_1, OK)
        response.setContent(copiedBuffer("neo", Utf8))
        Future.value(response)
      }
    }
    ServerBuilder().codec(Http()).bindTo(new InetSocketAddress("localhost", 6666)).name("testserver").build(new Respond)
  }

  before {
    server = startServer
  }

  it should "execute a request" in {
      val client = new RestClient(ClientConfiguration("localhost", 6666,
        FiniteDuration(10, TimeUnit.SECONDS)))
      val result = client.makeRequest("/")
      result successful { res =>
        res.getStatus.getCode should equal(200)
        res.getContent.toString(Charset.forName("UTF-8")) should equal("neo")
      }
    }


  it should "execute a request with a custom parser" in {
      val client = new RestClient(ClientConfiguration("localhost", 6666,
        FiniteDuration(10, TimeUnit.SECONDS)))
      implicit val parser = new ResultParser[String] {
        override def parseResult(response: HttpResponse): String = {
          response.getContent.toString(Charset.forName("UTF-8"))
        }
      }
      val result: Future[String] = client.makeRequest("/")
      result successful { res =>
        res should equal("neo")
      }
  }

  after {
    server.close()
  }

}
