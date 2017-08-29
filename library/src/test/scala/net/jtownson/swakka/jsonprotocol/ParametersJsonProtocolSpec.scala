package net.jtownson.swakka.jsonprotocol

import net.jtownson.swakka.jsonprotocol.ParametersJsonProtocol._
import net.jtownson.swakka.jsonschema.SchemaWriter._
import net.jtownson.swakka.model.Parameters._
import org.scalatest.Matchers._
import org.scalatest._
import shapeless.{::, HNil}
import spray.json.{JsArray, JsBoolean, JsObject, JsString, _}

class ParametersJsonProtocolSpec extends FlatSpec {

  // TODO: is there a way of generating table driven tests over type params??

  "ParametersJsonProtocol" should "serialize required query parameters" in {

    val params = QueryParameter[String]('qp, Some("a description")) :: HNil

    params.toJson shouldBe queryParamJson(true)
  }

  it should "serialize optional query parameters" in {

    val params = QueryParameter[Option[String]]('qp, Some("a description")) :: HNil

    params.toJson shouldBe queryParamJson(false)
  }

  it should "serialize required header parameters" in {

    val headers = HeaderParameter[String](Symbol("x-my-header"), Some("a header")) :: HNil

    headers.toJson shouldBe headerParamJson(true)
  }

  it should "serialize optional header parameters" in {

    val headers = HeaderParameter[Option[String]](Symbol("x-my-header"), Some("a header")) :: HNil

    headers.toJson shouldBe headerParamJson(false)
  }

  it should "serialize required path parameters" in {

    val params = PathParameter[String]('petId) :: HNil

    params.toJson shouldBe pathParameterJson(true)
  }

  it should "serialize optional path parameters" in {

    val params = PathParameter[Option[String]]('petId) :: HNil

    params.toJson shouldBe pathParameterJson(false)
  }

  case class Pet(petName: String)

  implicit val petSchemaWriter = schemaWriter(Pet)

  it should "serialize required body parameters" in {

    val params = BodyParameter[Pet]('pet, Some("a description")) :: HNil

    params.toJson shouldBe bodyParameterJson(true)
  }

  it should "serialize optional body parameters" in {

    val params = BodyParameter[Option[Pet]]('pet, Some("a description")) :: HNil

    params.toJson shouldBe bodyParameterJson(false)
  }

  it should "implicitly serialize hnil" in {

    type Params = HNil

    val params: Params = HNil

    val expectedJson = JsArray()

    params.toJson shouldBe expectedJson
  }

  it should "serialize an hlist of query params" in {

    type Params =
      QueryParameter[Int] :: QueryParameter[String] ::
        QueryParameter[Int] :: QueryParameter[String] :: HNil

    val params =
      QueryParameter[Int]('r) :: QueryParameter[String]('s) ::
        QueryParameter[Int]('t) :: QueryParameter[String]('u) :: HNil

    val expectedJson = JsArray(
      JsObject(
        "name" -> JsString("r"),
        "in" -> JsString("query"),
        "required" -> JsBoolean(true),
        "type" -> JsString("integer"),
        "format" -> JsString("int32")
      ),
      JsObject(
        "name" -> JsString("s"),
        "in" -> JsString("query"),
        "required" -> JsBoolean(true),
        "type" -> JsString("string")
      ),
      JsObject(
        "name" -> JsString("t"),
        "in" -> JsString("query"),
        "required" -> JsBoolean(true),
        "type" -> JsString("integer"),
        "format" -> JsString("int32")
      ),
      JsObject(
        "name" -> JsString("u"),
        "in" -> JsString("query"),
        "required" -> JsBoolean(true),
        "type" -> JsString("string")
      ))

    params.toJson shouldBe expectedJson
  }

  import net.jtownson.swakka.routegen.Tuplers._
  import FormParameterType._

  it should "serialize single-field, string form params" in {

    implicit val formParamFormat = requiredFormParameterFormat(Pet)

    val params = FormParameter[(String), Pet](
      'f, Some("form description"),
      construct = Pet) :: HNil

    params.toJson shouldBe JsArray(
      JsObject(
        "name" -> JsString("petName"),
        "in"-> JsString("formData"),
        "required" -> JsBoolean(true),
        "type" -> JsString("string")
      )
    )
  }

  case class BigPet(id: Int, petName: String, weight: Float)

  it should "serialize multi-field, form params" in {

    implicit val formParamFormat = requiredFormParameterFormat(BigPet)

    val params = FormParameter[(Int, String, Float), BigPet](
      'f, Some("form description"),
      construct = BigPet) :: HNil

    params.toJson shouldBe JsArray(
      JsObject(
        "name" -> JsString("id"),
        "in"-> JsString("formData"),
        "required" -> JsBoolean(true),
        "type" -> JsString("integer"),
        "format" -> JsString("int32")
      ),
      JsObject(
        "name" -> JsString("petName"),
        "in"-> JsString("formData"),
        "required" -> JsBoolean(true),
        "type" -> JsString("string")
      ),
      JsObject(
        "name" -> JsString("weight"),
        "in"-> JsString("formData"),
        "required" -> JsBoolean(true),
        "type" -> JsString("number"),
        "format" -> JsString("float")
      )
    )
  }

  private def queryParamJson(required: Boolean) =
    JsArray(
      JsObject(
        "name" -> JsString("qp"),
        "in" -> JsString("query"),
        "description" -> JsString("a description"),
        "required" -> JsBoolean(required),
        "type" -> JsString("string")
      )
    )

  private def pathParameterJson(required: Boolean) = {
    val expectedJson = JsArray(
      JsObject(
        "name" -> JsString("petId"),
        "in" -> JsString("path"),
        "required" -> JsBoolean(required),
        "type" -> JsString("string")
      )
    )
    expectedJson
  }

  private def bodyParameterJson(required: Boolean) =
    JsArray(
      JsObject(
        "name" -> JsString("pet"),
        "in" -> JsString("body"),
        "description" -> JsString("a description"),
        "required" -> JsBoolean(required),
        "schema" -> JsObject(
          "type" -> JsString("object"),
          "required" -> JsArray(JsString("petName")),
          "properties" -> JsObject(
            "petName" -> JsObject(
              "type" -> JsString("string")
            )
          )
        )
      )
    )

  private def headerParamJson(required: Boolean) =
    JsArray(
      JsObject(
        "name" -> JsString("x-my-header"),
        "in" -> JsString("header"),
        "description" -> JsString("a header"),
        "required" -> JsBoolean(required),
        "type" -> JsString("string")
      )
    )
}
