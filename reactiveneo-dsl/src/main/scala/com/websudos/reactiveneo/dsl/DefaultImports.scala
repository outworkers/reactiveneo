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
package com.websudos.reactiveneo.dsl

import com.websudos.reactiveneo.query.DefaultFormatters.{BooleanFormatter, DoubleFormatter, IntegerFormatter, StringFormatter}

/**
 * Implicit definitions used in DSL.
 */
trait DefaultImports extends MatchQueryImplicits with ImplicitConversions {

  implicit val stringFormatter = new StringFormatter
  implicit val intFormatter = new IntegerFormatter
  implicit val doubleFormatter = new DoubleFormatter
  implicit val booleanFormatter = new BooleanFormatter

}
