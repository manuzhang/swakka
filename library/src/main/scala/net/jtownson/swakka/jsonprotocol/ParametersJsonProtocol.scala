package net.jtownson.swakka.jsonprotocol

import net.jtownson.swakka.jsonschema.ApiModelDictionary._
import net.jtownson.swakka.jsonschema.{JsonSchema, SchemaWriter}
import shapeless.{::, HList, HNil}
import spray.json.{JsArray, JsBoolean, JsString, JsValue}
import ParameterJsonFormat.func2Format
import net.jtownson.swakka.misc.jsObject
import net.jtownson.swakka.model.Parameters.{BodyParameter, HeaderParameter, PathParameter, QueryParameter}

import scala.reflect.runtime.universe.TypeTag

trait ParametersJsonProtocol {

  implicit val strReqQueryParamFormat: ParameterJsonFormat[QueryParameter[String]] =
    (qp: QueryParameter[String]) => simpleParam(qp.name, "query", qp.description, true, "string", None)

  implicit val strOptQueryParamFormat: ParameterJsonFormat[QueryParameter[Option[String]]] =
    (qp: QueryParameter[Option[String]]) => simpleParam(qp.name, "query", qp.description, false, "string", None)

  implicit val floatReqQueryParamFormat: ParameterJsonFormat[QueryParameter[Float]] =
    (qp: QueryParameter[Float]) => simpleParam(qp.name, "query", qp.description, true, "number", Some("float"))

  implicit val floatOptQueryParamFormat: ParameterJsonFormat[QueryParameter[Option[Float]]] =
    (qp: QueryParameter[Option[Float]]) => simpleParam(qp.name, "query", qp.description, false, "number", Some("float"))

  implicit val doubleReqQueryParamFormat: ParameterJsonFormat[QueryParameter[Double]] =
    (qp: QueryParameter[Double]) => simpleParam(qp.name, "query", qp.description, true, "number", Some("double"))

  implicit val doubleOptQueryParamFormat: ParameterJsonFormat[QueryParameter[Option[Double]]] =
    (qp: QueryParameter[Option[Double]]) => simpleParam(qp.name, "query", qp.description, false, "number", Some("double"))

  implicit val booleanReqQueryParamFormat: ParameterJsonFormat[QueryParameter[Boolean]] =
    (qp: QueryParameter[Boolean]) => simpleParam(qp.name, "query", qp.description, true, "boolean", None)

  implicit val booleanOptQueryParamFormat: ParameterJsonFormat[QueryParameter[Option[Boolean]]] =
    (qp: QueryParameter[Option[Boolean]]) => simpleParam(qp.name, "query", qp.description, false, "boolean", None)

  implicit val intReqQueryParamFormat: ParameterJsonFormat[QueryParameter[Int]] =
    (qp: QueryParameter[Int]) => simpleParam(qp.name, "query", qp.description, true, "integer", Some("int32"))

  implicit val intOptQueryParamFormat: ParameterJsonFormat[QueryParameter[Option[Int]]] =
    (qp: QueryParameter[Option[Int]]) => simpleParam(qp.name, "query", qp.description, false, "integer", Some("int32"))

  implicit val longReqQueryParamFormat: ParameterJsonFormat[QueryParameter[Long]] =
    (qp: QueryParameter[Long]) => simpleParam(qp.name, "query", qp.description, true, "integer", Some("int64"))

  implicit val longOptQueryParamFormat: ParameterJsonFormat[QueryParameter[Option[Long]]] =
    (qp: QueryParameter[Option[Long]]) => simpleParam(qp.name, "query", qp.description, false, "integer", Some("int64"))

  implicit val strReqPathParamFormat: ParameterJsonFormat[PathParameter[String]] =
    (pp: PathParameter[String]) => simpleParam(pp.name, "path", pp.description, true, "string", None)

  implicit val strOptPathParamFormat: ParameterJsonFormat[PathParameter[Option[String]]] =
    (pp: PathParameter[Option[String]]) => simpleParam(pp.name, "path", pp.description, false, "string", None)

  implicit val floatReqPathParamFormat: ParameterJsonFormat[PathParameter[Float]] =
    (pp: PathParameter[Float]) => simpleParam(pp.name, "path", pp.description, true, "number", Some("float"))

  implicit val floatOptPathParamFormat: ParameterJsonFormat[PathParameter[Option[Float]]] =
    (pp: PathParameter[Option[Float]]) => simpleParam(pp.name, "path", pp.description, false, "number", Some("float"))

  implicit val doubleReqPathParamFormat: ParameterJsonFormat[PathParameter[Double]] =
    (pp: PathParameter[Double]) => simpleParam(pp.name, "path", pp.description, true, "number", Some("double"))

  implicit val doubleOptPathParamFormat: ParameterJsonFormat[PathParameter[Option[Double]]] =
    (pp: PathParameter[Option[Double]]) => simpleParam(pp.name, "path", pp.description, false, "number", Some("double"))

  implicit val booleanReqPathParamFormat: ParameterJsonFormat[PathParameter[Boolean]] =
    (pp: PathParameter[Boolean]) => simpleParam(pp.name, "path", pp.description, true, "boolean", None)

  implicit val booleanOptPathParamFormat: ParameterJsonFormat[PathParameter[Option[Boolean]]] =
    (pp: PathParameter[Option[Boolean]]) => simpleParam(pp.name, "path", pp.description, false, "boolean", None)

  implicit val intReqPathParamFormat: ParameterJsonFormat[PathParameter[Int]] =
    (pp: PathParameter[Int]) => simpleParam(pp.name, "path", pp.description, true, "integer", Some("int32"))

  implicit val intOptPathParamFormat: ParameterJsonFormat[PathParameter[Option[Int]]] =
    (pp: PathParameter[Option[Int]]) => simpleParam(pp.name, "path", pp.description, false, "integer", Some("int32"))

  implicit val longReqPathParamFormat: ParameterJsonFormat[PathParameter[Long]] =
    (pp: PathParameter[Long]) => simpleParam(pp.name, "path", pp.description, true, "integer", Some("int64"))

  implicit val longOptPathParamFormat: ParameterJsonFormat[PathParameter[Option[Long]]] =
    (pp: PathParameter[Option[Long]]) => simpleParam(pp.name, "path", pp.description, false, "integer", Some("int64"))

  implicit val strReqHeaderParamFormat: ParameterJsonFormat[HeaderParameter[String]] =
    (hp: HeaderParameter[String]) => simpleParam(hp.name, "header", hp.description, true, "string", None)

  implicit val strOptHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Option[String]]] =
    (hp: HeaderParameter[Option[String]]) => simpleParam(hp.name, "header", hp.description, false, "string", None)

  implicit val floatReqHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Float]] =
    (hp: HeaderParameter[Float]) => simpleParam(hp.name, "header", hp.description, true, "number", Some("float"))

  implicit val floatOptHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Option[Float]]] =
    (hp: HeaderParameter[Option[Float]]) => simpleParam(hp.name, "header", hp.description, false, "number", Some("float"))

  implicit val doubleReqHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Double]] =
    (hp: HeaderParameter[Double]) => simpleParam(hp.name, "header", hp.description, true, "number", Some("double"))

  implicit val doubleOptHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Option[Double]]] =
    (hp: HeaderParameter[Option[Double]]) => simpleParam(hp.name, "header", hp.description, false, "number", Some("double"))

  implicit val booleanReqHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Boolean]] =
    (hp: HeaderParameter[Boolean]) => simpleParam(hp.name, "header", hp.description, true, "boolean", None)

  implicit val booleanOptHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Option[Boolean]]] =
    (hp: HeaderParameter[Option[Boolean]]) => simpleParam(hp.name, "header", hp.description, false, "boolean", None)

  implicit val intReqHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Int]] =
    (hp: HeaderParameter[Int]) => simpleParam(hp.name, "header", hp.description, true, "integer", Some("int32"))

  implicit val intOptHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Option[Int]]] =
    (hp: HeaderParameter[Option[Int]]) => simpleParam(hp.name, "header", hp.description, false, "integer", Some("int32"))

  implicit val longReqHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Long]] =
    (hp: HeaderParameter[Long]) => simpleParam(hp.name, "header", hp.description, true, "integer", Some("int64"))

  implicit val longOptHeaderParamFormat: ParameterJsonFormat[HeaderParameter[Option[Long]]] =
    (hp: HeaderParameter[Option[Long]]) => simpleParam(hp.name, "header", hp.description, false, "integer", Some("int64"))

  implicit def requiredBodyParamFormat[T: TypeTag](implicit ev: SchemaWriter[T]): ParameterJsonFormat[BodyParameter[T]] = {

    implicit val dict = apiModelDictionary[T]

    func2Format((bp: BodyParameter[T]) => bodyParameter(ev, bp.name, bp.description, true))
  }

  implicit def optionalBodyParamFormat[T: TypeTag](implicit ev: SchemaWriter[T]): ParameterJsonFormat[BodyParameter[Option[T]]] = {

    implicit val dict = apiModelDictionary[T]

    func2Format((bp: BodyParameter[Option[T]]) => bodyParameter(ev, bp.name, bp.description, false))
  }

  private def bodyParameter[T: TypeTag](ev: SchemaWriter[T], name: Symbol,
                                        description: Option[String], required: Boolean) = {
    jsObject(
      Some("name" -> JsString(name.name)),
      Some("in" -> JsString("body")),
      description.map("description" -> JsString(_)),
      Some("required" -> JsBoolean(required)),
      Some("schema" -> ev.write(JsonSchema[T]()))
    )
  }

  implicit val hNilParamFormat: ParameterJsonFormat[HNil] =
    _ => JsArray()

  implicit def hConsParamFormat[H, T <: HList](implicit head: ParameterJsonFormat[H], tail: ParameterJsonFormat[T]): ParameterJsonFormat[H :: T] =
    func2Format((l: H :: T) => {
      Flattener.flattenToArray(JsArray(head.write(l.head), tail.write(l.tail)))
    })

  private def simpleParam(name: Symbol, in: String, description: Option[String], required: Boolean, `type`: String, format: Option[String]): JsValue =
    jsObject(
      Some("name" -> JsString(name.name)),
      Some("in" -> JsString(in)),
      description.map("description" -> JsString(_)),
      Some("required" -> JsBoolean(required)),
      Some("type" -> JsString(`type`)),
      format.map("format" -> JsString(_))
    )

}

object ParametersJsonProtocol extends ParametersJsonProtocol