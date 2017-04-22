package net.jtownson.swakka.jsonprotocol

import Flattener.flattenToObject
import net.jtownson.swakka.OpenApiModel.ResponseValue
import net.jtownson.swakka.jsonprotocol.ResponseJsonFormat._
import net.jtownson.swakka.jsonschema.{JsonSchema, SchemaWriter}
import shapeless.{::, HList, HNil}
import spray.json.{JsArray, JsObject, JsValue}

trait ResponsesJsonProtocol {

  implicit val strResponseFormat: ResponseJsonFormat[ResponseValue[String]] =
    (rv: ResponseValue[String]) => swaggerResponse(rv.responseCode, JsonSchema[String]())


  implicit val hNilResponseFormat: ResponseJsonFormat[HNil] =
    _ => JsObject()


  implicit def hConsResponseFormat[H, T <: HList](implicit head: ResponseJsonFormat[H], tail: ResponseJsonFormat[T]): ResponseJsonFormat[H :: T] =
    func2Format((l: H :: T) => {
      flattenToObject(JsArray(head.write(l.head), tail.write(l.tail)))
    })


  import scala.reflect.runtime.universe._

  implicit def caseClassResponseFormat0[T <: Product: TypeTag : SchemaWriter](constructor: () => T):
  ResponseJsonFormat[ResponseValue[T]] =
    caseClassFormat[T]

  implicit def caseClassResponseFormat1[T <: Product: TypeTag : SchemaWriter,
                                        F1: SchemaWriter](constructor: (F1) => T):
  ResponseJsonFormat[ResponseValue[T]] =
    caseClassFormat[T]


  private def caseClassFormat[T <: Product : TypeTag : SchemaWriter]: ResponseJsonFormat[ResponseValue[T]] =
    func2Format((rv: ResponseValue[T]) => swaggerResponse(rv.responseCode, JsonSchema[T]()))

  private def swaggerResponse[T](status: Int, schema: JsonSchema[T])
                                (implicit sw: SchemaWriter[T]): JsValue =
    JsObject(
      String.valueOf(status) -> JsObject(
        "schema" -> sw.write(schema)
      )
    )
}

object ResponsesJsonProtocol extends ResponsesJsonProtocol