package net.jtownson.swakka

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.directives.MarshallingDirectives
import akka.http.scaladsl.server.{MalformedQueryParamRejection, MalformedRequestContentRejection}
import akka.http.scaladsl.testkit.{RouteTest, TestFrameworkInterface}
import net.jtownson.swakka.ConvertibleToDirective0._
import net.jtownson.swakka.OpenApiModel._
import net.jtownson.swakka.RouteGen._
import net.jtownson.swakka.SwaggerRoute.swaggerRoute
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import org.scalatest.Inside._
import org.scalatest.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._
import shapeless.{::, HNil}
import spray.json._
import spray.json.DefaultJsonProtocol._

class RouteGenSpec extends FlatSpec with MockFactory with RouteTest with TestFrameworkInterface {

  val f = mockFunction[HttpRequest, ToResponseMarshallable]

  private val defaultItem = PathItem[HNil, ResponseValue[String] :: HNil](GET, Operation(HNil, ResponseValue[String](200) :: HNil, f))

  val zeroParamModels = Table(
    ("testcase name", "request", "model", "response"),
    ("index page", get("/"), Endpoint("/", defaultItem), "YES"),
    ("simple path", get("/ruok"), Endpoint("/ruok", defaultItem), "YES"),
    ("missing base path", get("/ruok"), Endpoint("ruok", defaultItem), "YES"),
    ("complex path", get("/ruok/json"), Endpoint("ruok/json", defaultItem), "YES")
  )

  forAll(zeroParamModels) { (testcaseName, request, apiModel, response) =>
    testcaseName should "convert to a complete akka Route" in {

      f expects request returning response

      val route = RouteGen.endpointRoute(apiModel)

      request ~> route ~> check {
        status shouldBe OK
        responseAs[String] shouldBe response
      }
    }
  }

  type OneStringParam = QueryParameter[String] :: HNil

  private val itemWithQueryParam = PathItem[OneStringParam, ResponseValue[String] :: HNil](
    GET, Operation(QueryParameter[String]('q) :: HNil, ResponseValue[String](200) :: HNil, f))

  val oneStrParamModels = Table(
    ("testcase name", "request", "model", "response"),
    ("echo query", get("/app?q=x"), Endpoint("/app", itemWithQueryParam), "x")
  )

  forAll(oneStrParamModels) { (testcaseName, request, apiModel, response) =>
    testcaseName should "convert to a complete akka Route" in {

      f expects request returning response

      val route = RouteGen.endpointRoute(apiModel)

      request ~> route ~> check {
        status shouldBe OK
        responseAs[String] shouldBe response
      }
    }
  }

  type OneIntParam = QueryParameter[Int] :: HNil

  val itemWithIntParam = PathItem[OneIntParam, ResponseValue[String] :: HNil](
    GET, Operation(QueryParameter[Int]('q) :: HNil, ResponseValue[String](200) :: HNil, f))

  "int params that are NOT ints" should "be rejected" in {

    val request = get("/app?q=x")

    val route = RouteGen.endpointRoute(Endpoint("/app", itemWithIntParam))

    request ~> route ~> check {
      inside(rejection) { case MalformedQueryParamRejection(parameterName, _, _) =>
        parameterName shouldBe "q"
      }
    }
  }

  "int params that are ints" should "be passed" in {

    val request = get("/app?q=10")

    f expects request returning "x"

    val route = RouteGen.endpointRoute(Endpoint("/app", itemWithIntParam))

    request ~> route ~> check {
      status shouldBe OK
      responseAs[String] shouldBe "x"
    }
  }

  case class Pet(name: String)

  implicit val petFormat = jsonFormat1(Pet)

  type Params = BodyParameter[Pet] :: HNil
  type Responses = ResponseValue[String] :: HNil

  val itemWithBodyParam = PathItem[Params, Responses](
    HttpMethods.POST, Operation(BodyParameter[Pet]('pet) :: HNil, ResponseValue[String](200) :: HNil, f))

  val animalRoute = RouteGen.endpointRoute(Endpoint("/app", itemWithBodyParam))

  "body params of correct type" should "be marshallable" in {

    val request = post("/app", Pet("tiddles"))

    f expects request returning "x"

    request ~> animalRoute ~> check {
      status shouldBe OK
    }
  }

  case class WildAnimal(species: String)

  implicit val wildAnimalFormat = jsonFormat1(WildAnimal)

  "body params of wrong type" should "be rejected" in {

    post("/app", WildAnimal("lion")) ~> animalRoute ~> check {
      inside(rejection) {
        case MalformedRequestContentRejection(message, _) =>
          message shouldBe "Object is missing required member 'name'"
      }
    }
  }

  "body params" should "be easy to handle in endpoint impls" in {

    import MarshallingDirectives._

    val f: HttpRequest => ToResponseMarshallable =
      (request: HttpRequest) => as[Pet].apply(request)

    val itemWithBodyParam = PathItem[Params, Responses](
      HttpMethods.POST, Operation(BodyParameter[Pet]('pet) :: HNil, ResponseValue[String](200) :: HNil, f))

    val animalRoute = RouteGen.endpointRoute(Endpoint("/app", itemWithBodyParam))

    val request = post("/app", Pet("tiddles"))

    request ~> animalRoute ~> check {
      status shouldBe OK
      responseAs[Pet] shouldBe Pet("tiddles")
    }

  }

  "multiple endpoints" should "work" in {

    val f1 = mockFunction[HttpRequest, ToResponseMarshallable]
    val f2 = mockFunction[HttpRequest, ToResponseMarshallable]

    val endpoint1: Endpoint[OneIntParam, ::[ResponseValue[String], HNil]] = Endpoint(
      "/app/e1", PathItem[OneIntParam, ResponseValue[String] :: HNil](
      GET, Operation(QueryParameter[Int]('q) :: HNil, ResponseValue[String](200) :: HNil, f1)))

    val endpoint2 = Endpoint(
      "/app/e2", PathItem[OneStringParam, ResponseValue[String] :: HNil](
      GET, Operation(QueryParameter[String]('q) :: HNil, ResponseValue[String](200) :: HNil, f2)))


    val api = OpenApi(endpoint1 :: endpoint2 :: HNil)

    val route = RouteGen.openApiRoute(api)

    val e1Request = get("/app/e1?q=10")
    val e2Request = get("/app/e2?q=str")

    f1 expects e1Request returning "e1-response"
    f2 expects e2Request returning "e2-response"

    e1Request ~> route ~> check {
      status shouldBe OK
      responseAs[String] shouldBe "e1-response"
    }

    e2Request ~> route ~> check {
      status shouldBe OK
      responseAs[String] shouldBe "e2-response"
    }
  }

  private def get(path: String): HttpRequest = {
    Get(s"http://example.com$path")
  }

  private def post[T: JsonWriter](path: String, t: T): HttpRequest = {
    HttpRequest(HttpMethods.POST,
      uri = "http://example.com/app",
      entity = HttpEntity(ContentTypes.`application/json`, t.toJson.prettyPrint))
  }

  override def failTest(msg: String): Nothing = throw new AssertionError(msg)
}