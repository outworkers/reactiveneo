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

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

/**
 * Base class implementing skeleton for paring the result.
 *
 * ```
 * {
 *   "results":[{
 *     "columns":["a","b"],
 *     "data":[
 *       {
 *         "row":[{"name":"Michael"},{"something"}]
 *       }
 *     ]
 *   }],
 *   "errors":[]
 * }
 * ```
 *
 */
class CypherResultParser[R](implicit parser: Reads[R]) extends JsonParser[Seq[R]] {

  implicit val readError: Reads[ErrorMessage] = {
    (
      (__ \ "code").read[String] and (__ \ "message").read[String]
      )(ErrorMessage.apply _)
  }

  override def parseResult(js: JsValue): Seq[R] = {
    (js \ "results").as[JsArray].value.toList match {
      case result :: Nil => {
        val rows = (result \ "data").as[JsArray].value.map(row => (row \ "row")(0))
        val parsed = rows.map(row => row.validate[R])
        if(parsed.forall(res => res.isSuccess)) {
          parsed.map(_.get)
        } else {
          throw new JsonValidationException(buildErrorMessage(parsed.filter(_.isError).head.asInstanceOf[JsError]))
        }
      }
      case _ => {
        val errorReads =  (__ \ "errors").read[Seq[ErrorMessage]]
        val error = errorReads.reads(js)
        error match {
          case JsSuccess(e, _) => throw new RestClientException(e)
          case e: JsError => throw new JsonValidationException(buildErrorMessage(e))
        }
      }
    }
  }

}

case class ErrorMessage(code: String, msg: String)
