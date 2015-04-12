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

import com.websudos.reactiveneo.RequiresNeo4jServer
import com.websudos.reactiveneo.client.RestClient._
import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}

import scala.concurrent.duration.FiniteDuration

class RestClientTest extends FlatSpec with Matchers with ScalaFutures with IntegrationPatience {

  it should "execute a request" taggedAs RequiresNeo4jServer in {
    val client = new RestClient(ClientConfiguration("localhost", 7474, FiniteDuration(10, TimeUnit.SECONDS)))
    val result = client.makeRequest("/")
    whenReady(result) { res =>
      res.getStatus.getCode should equal(200)
      res.getContent.toString(Charset.forName("UTF-8")) should include("http://localhost/db/manage/")
      res.getContent.toString(Charset.forName("UTF-8")) should not contain "error"
    }
  }


}
