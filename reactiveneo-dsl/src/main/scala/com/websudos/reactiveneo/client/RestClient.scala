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
import java.util.concurrent.TimeUnit

import com.twitter.finagle.{Http, Service}
import com.twitter.util
import com.twitter.util.{Duration, Future, JavaTimer, Timer}
import com.typesafe.scalalogging.slf4j.StrictLogging
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http._

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success, Try}

object RestClient {

  implicit lazy val dummyParser = new DummyParser
}

/**
  * REST client implementation based on Finagle RPC.
 */
class RestClient(config: ClientConfiguration) extends StrictLogging {


  lazy val client: Service[HttpRequest, HttpResponse] = Http.newService(s"${config.server}:${config.port}")

  implicit lazy val timer: Timer = new JavaTimer()

  /**
   * Execute the request with given parser.
   * @param path Path to execute the request against.
   * @param parser Parser used to parse the result.
   * @tparam R Type of result
   * @return Returns future of parsed result object.
   */
  def makeRequest[R](path: String, method: HttpMethod = HttpMethod.GET, content: Option[String] = None)
                 (implicit parser: ResultParser[R]): concurrent.Future[R] = {
    val request =  new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, path)
    content.foreach { body =>
      request.setContent(ChannelBuffers.copiedBuffer(body, Charset.forName("UTF-8")))
    }
    request.headers()
      .add("Content-Type", "application/json")
      .add("Host",config.server)

    val response: util.Future[HttpResponse] = client(request)
    response onSuccess { resp: HttpResponse =>
      logger.debug("GET success: " + resp)
    }
    val timeout: Duration = Duration(config.defaultTimeout.toMillis, TimeUnit.MILLISECONDS)
    val res = response.raiseWithin(timeout).map(parser.parseResult).flatMap {
      case Success(s) => Future(s)
      case Failure(f) => Future.exception(f)
    }
    val promise = Promise[R]()
    res.onSuccess(promise.success(_))
    res.onFailure(promise.failure(_))
    promise.future
  }

}

/**
 * Result parser is used to parse REST response object to a meaningful business object.
 * @tparam R type of resulting object.
 */
trait ResultParser[+R] {

  /**
   * Parse the HttpResponse object to a business object. In case of response status being invalid or response data
   * corrupted Left with corresponding message should be returned. Otherwise the funciton should return Right
   *
   * @param response HttpResponse object.
   * @return Result of parsing.
   */
  def parseResult(response: HttpResponse): Try[R]
}

/**
 * Dummy parser used when no parsing is required.
 */
class DummyParser extends ResultParser[HttpResponse] {
  override def parseResult(response: HttpResponse): Try[HttpResponse] = Try(response)
}


case class ClientConfiguration(server: String, port: Int, defaultTimeout: FiniteDuration)
