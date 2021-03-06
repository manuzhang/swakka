package net.jtownson.swakka.openapijson

import net.jtownson.swakka.openapimodel._
import org.scalatest.FlatSpec
import org.scalatest.Matchers._
import spray.json._
import shapeless.{HNil, ::}

// Investigate the usage of three styles of response definition
// 1) Case classes
// 2) HLists
// 3) Tuples
class ResponseProductSpec extends FlatSpec {

  "Zero response formats" should "work for products" in {

    case class EmptyResponses()

    implicitly[ResponseJsonFormat[HNil]]

    implicitly[ResponseJsonFormat[Unit]]

    implicitly[ResponseJsonFormat[EmptyResponses]]
  }

  "Single response formats" should "work for products" in {

    type ResponseType = ResponseValue[String, Header[Long]]

    case class SingleResponse(r1: ResponseType)

    implicitly[ResponseJsonFormat[ResponseType :: HNil]]

    implicitly[ResponseJsonFormat[Tuple1[ResponseType]]]

    implicitly[ResponseJsonFormat[SingleResponse]]
  }

  "Multiple response formats" should "work for products" in {

    type ResponseType1 = ResponseValue[String, Header[Long]]
    type ResponseType2 = ResponseValue[Boolean, Header[String]]

    case class MultipleResponse(r1: ResponseType1, r2: ResponseType2)

    implicitly[ResponseJsonFormat[ResponseType1 :: ResponseType2 :: HNil]]

    implicitly[ResponseJsonFormat[(ResponseType1, ResponseType2)]]

    implicitly[ResponseJsonFormat[MultipleResponse]]
  }
}
