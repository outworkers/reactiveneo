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

/**
 * The first class citizen in the DSL implementation serving the purpose of defining node metadata.
 *
 * User needs to extend this class when defining nodes he/she wants to use in the queries.
 */
abstract class Node[Owner <: Node[Owner, Record], Record]
  extends GraphObject[Owner, Record] {

}

