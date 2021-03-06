/*
 * Copyright 2017 Jeremy Townson
 *
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

package net.jtownson.swakka.jsonschema

import spray.json.JsValue


trait SchemaWriter[T] {
  def write(schema: JsonSchema[T]): JsValue
}

object SchemaWriter {

  def apply[T](implicit ev: SchemaWriter[T]): SchemaWriter[T] = ev

  def instance[T](f: JsonSchema[T] => JsValue): SchemaWriter[T] = new SchemaWriter[T] {
    override def write(schema: JsonSchema[T]): JsValue = f(schema)
  }
}