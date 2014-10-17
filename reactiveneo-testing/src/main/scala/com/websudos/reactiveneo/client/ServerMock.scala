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

import java.net.InetSocketAddress

import com.newzly.util.testing.AsyncAssertionsHelper._
import com.twitter.finagle.Service
import com.twitter.finagle.builder.{ServerBuilder, Server}
import com.twitter.finagle.http.Http
import com.twitter.io.Charsets._
import com.twitter.util.Future
import org.jboss.netty.buffer.ChannelBuffers._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.handler.codec.http.{DefaultHttpResponse, HttpRequest, HttpResponse}
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

/**
 * A simple server to be used in testing. Server is occupying a socket and therefore should be closed after the test.
 */
class ServerMock(handler: (HttpRequest) => HttpResponse) {

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  private val address = new InetSocketAddress("localhost", 0)

  val server: Server = {
    class Respond extends Service[HttpRequest, HttpResponse] {
      def apply(request: HttpRequest) = {
        val response = handler(request)
        Future.value(response)
      }
    }
    ServerBuilder().codec(Http()).bindTo(address).name("testserver").build(new Respond)
  }

  val port = server.localAddress.asInstanceOf[InetSocketAddress].getPort

  val host = server.localAddress.asInstanceOf[InetSocketAddress].getHostName

  def close() {
    server.close()
  }

}

trait ServerMockSugar {

  implicit def fromString(contents: String) = {
    val response = new DefaultHttpResponse(HTTP_1_1, OK)
    response.setContent(copiedBuffer(contents, Utf8))
    response
  }

  def withServer(handler: (HttpRequest) => HttpResponse, block: InetSocketAddress => Unit): Unit = {
    val server = new ServerMock(handler)
    block(server.server.localAddress.asInstanceOf[InetSocketAddress])
  }

}