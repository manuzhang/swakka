package net.jtownson.swakka.routegen

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.{host, pass}

object hostDirective {
  def apply(apiHost: Option[String]): Directive0 =
    apiHost match {
      case Some(hostName) => host(hostName)
      case None => pass
    }
}
