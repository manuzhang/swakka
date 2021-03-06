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

package net.jtownson.swakka.misc

import scala.reflect.runtime.universe._

// Who says scala reduces boilerplate ?!!?
object AnnotationExtractor {

  def constructorAnnotations[T: TypeTag](annotationClass: Class[_]): Map[String, Set[(String, String)]] = {
    constructorAnnotationsRaw(annotationClass).
      groupBy(_._1).
      map((t: (String, Set[(String, String, String)])) => (t._1, drop1(t._2)))
  }

  private def drop1(s: Set[(String, String, String)]): Set[(String, String)] = {
    s.map(sss => (sss._2, sss._3))
  }

  private def constructorAnnotationsRaw[T: TypeTag](annotationClass: Class[_]): Set[(String, String, String)] = {

    val tpe = typeOf[T]

    val constructor: MethodSymbol = primaryConstructor(tpe.decl(termNames.CONSTRUCTOR))

    val params: Seq[Symbol] = constructor.paramLists.flatten

    val annotatedParams: Seq[(String, Annotation)] = params.flatMap(param => tuple(param, annotationClass.getName))

    annotatedParams.flatMap(treeIntrospect).toSet
  }

  private def treeIntrospect[T: TypeTag](annotatedParam: (String, Annotation)): Seq[(String, String, String)] = {

    val (field, annotation) = annotatedParam

    val annotationValues: Seq[Tree] = annotation.tree.children.tail

    annotationValues.flatMap({
      case AssignOrNamedArg(Ident(name), Literal(value)) => {
        Some((field, name.decodedName.toString, value.value.toString))
      }
      case _ => None
    })
  }

  private def tuple(s: Symbol, annotationName: String): Option[(String, Annotation)] = {
    swaggerAnnotation(s.annotations, annotationName).map(annotation => (s.name.toString, annotation))
  }

  private def swaggerAnnotation(annotations: Seq[Annotation], annotationName: String): Option[Annotation] = {
    annotations.flatMap(annotation => annotation.tree.tpe.typeSymbol.fullName match {
      case s if s == annotationName => Some(annotation)
      case _ => None
    })
    annotations.headOption
  }

  private def primaryConstructor[T: TypeTag](constructorSymbol: Symbol) = {
    if (constructorSymbol.isMethod) {
      constructorSymbol.asMethod
    }
    else {
      val constructors = constructorSymbol.asTerm.alternatives
      constructors.map(_.asMethod).find(_.isPrimaryConstructor).get
    }
  }
}
