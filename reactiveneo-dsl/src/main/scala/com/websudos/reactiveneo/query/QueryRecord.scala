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
package com.websudos.reactiveneo.query

import play.api.libs.json.{Reads, JsObject}

/**
 * Wraps a single record result of Cypher query.
 */
class QueryRecord(obj: JsObject) {

  def attributes: IndexedSeq[String] = obj.fields.map(_._1).toIndexedSeq

  def apply[T](attributeName: String)(implicit parser: Reads[T]): Option[T] = {
    (obj \ attributeName).asOpt[T]
  }

}


object QueryRecord {
  def apply(obj: JsObject) = new QueryRecord(obj)
}