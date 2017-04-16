package net.jtownson.swakka

import spray.json.{JsValue, JsonFormat}

trait EndpointJsonFormat[T] extends JsonFormat[T] {
  def read(json: JsValue): T = throw new UnsupportedOperationException("Cannot read swagger files (yet).")
  def func2Format[T](f: T => JsValue): ParameterJsonFormat[T] = (obj: T) => f(obj)
}

object EndpointJsonFormat {
  def func2Format[T](f: T => JsValue): EndpointJsonFormat[T] = (obj: T) => f(obj)
}
