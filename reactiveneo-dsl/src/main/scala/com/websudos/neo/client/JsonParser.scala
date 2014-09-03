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

import java.nio.charset.Charset

import org.jboss.netty.handler.codec.http.{HttpResponse, HttpResponseStatus}
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.collection.immutable.Seq

/**
 * Parser abstraction to  used to parse JSON format of HttpResult content. To use this base class implementation of
 * a `reads` method needs to be provided.
 */
abstract class JsonParser[R] extends ResultParser[R] {

  /**
   * Implementation of of converter from JsValue to target type.
   * @return Returns converted value.
   */
  def reads: Reads[R]

  private def parseJson(s: String): R = {
    val json = Json.parse(s)
    reads.reads(json) match {
      case JsSuccess(value, _) => value
      case e: JsError => throw new JsonValidationException(buildErrorMessage(e))
    }
  }


  private[this] def singleErrorMessage(error: (JsPath, scala.Seq[ValidationError])) = {
    val (path: JsPath, errors: Seq[ValidationError]) = error
    val message = errors.foldLeft(errors.head.message)((acc,err) => s"$acc,${err.message}")
    s"Errors at $path: $message"
  }

  private[this] def buildErrorMessage(error: JsError) = {
    error.errors.tail.foldLeft(singleErrorMessage(error.errors.head))((acc,err) => s"acc,${singleErrorMessage(err)}")
  }

  override def parseResult(response: HttpResponse): R = {
    if(response.getStatus.getCode == HttpResponseStatus.OK.getCode) {
      parseJson(response.getContent.toString(Charset.forName("UTF-8")))
    } else {
      throw new InvalidResponseException(s"Response status <${response.getStatus}> is not valid")
    }
  }

}

/**
 * Exception indicating a problem when decoding resulting object value from JSON tree.
 * @param msg Error message.
 */
class JsonValidationException(msg: String) extends Exception

class InvalidResponseException(msg: String) extends Exception