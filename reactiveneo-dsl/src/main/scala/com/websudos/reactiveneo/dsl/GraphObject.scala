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

import com.websudos.reactiveneo.attribute.AbstractAttribute
import com.websudos.reactiveneo.query.QueryRecord

import scala.collection.mutable.{ArrayBuffer => MutableArrayBuffer, SynchronizedBuffer => MutableSyncBuffer}
import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.{currentMirror => cm, universe => ru}

/**
 * Base object for Node and Relationship. It encapsulates pattern builder and properties types.
 *
 * Greedy object initialisation is done in a thread safe way via a global lock acquired on a singleton case object,
 * preventing race conditions on multiple threads accessing the same table during initialisation.
 */
private[reactiveneo] abstract class GraphObject[Owner <: GraphObject[Owner, Record], Record] {

  /**
   * It allows DSL users to obtain good "default" values for either nodes or relationships names.
   */
  private[this] lazy val _name: String = {
    cm.reflect(this).symbol.name.toTypeName.decoded
  }

  private[this] lazy val _attributes: MutableArrayBuffer[AbstractAttribute[_]] =
    new MutableArrayBuffer[AbstractAttribute[_]] with MutableSyncBuffer[AbstractAttribute[_]]


  /**
   * The most notable and honorable of functions in this file, this is what allows our DSL to provide type-safety.
   * It works by requiring a user to define a type-safe mapping between a buffered Result and the above refined Record.
   *
   * Objects delimiting pre-defined columns also have a pre-defined "apply" method, allowing the user to simply autofill
   * the type-safe mapping by using pre-existing definitions.
   *
   * @param data The data incoming as a result from a query.
   * @return A Record instance.
   */
  def fromQuery(data: QueryRecord): Record

  /**
   * Symbolic name of this object - an alias.
   */
  def objectName: String = _name

  /**
   * List of [[com.websudos.reactiveneo.attribute.AbstractAttribute]]s  defined for this graph object.
   */
  def attributes: List[AbstractAttribute[_]] = _attributes.toList



  Lock.synchronized {
    val instanceMirror = cm.reflect(this)
    val selfType = instanceMirror.symbol.toType

    // Collect all attributes definitions starting from base class
    val columnMembers = MutableArrayBuffer.empty[Symbol]
    selfType.baseClasses.reverse.foreach {
      baseClass =>
        val baseClassMembers = baseClass.typeSignature.members.sorted
        val baseClassColumns = baseClassMembers.filter(_.typeSignature <:< ru.typeOf[AbstractAttribute[_]])
        baseClassColumns.foreach(symbol => if (!columnMembers.contains(symbol)) columnMembers += symbol)
    }

    columnMembers.foreach {
      symbol =>
        val column = instanceMirror.reflectModule(symbol.asModule).instance
        _attributes += column.asInstanceOf[AbstractAttribute[_]]
    }
  }
}

private[reactiveneo] case object Lock
