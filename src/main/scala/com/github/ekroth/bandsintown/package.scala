/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth

package object bandsintown extends Objects {
  case class Credentials(id: String) {
    def version: String = "2.0"
  }

  import errorhandling._

  object BandsintownError {
    case class Usage(errors: Seq[String]) extends Error {
      def reason = errors.toString
    }
    case class Json(error: play.api.libs.json.JsError, reason: String = "") extends Error
  }

  /*private[bandsintown]*/ implicit class RichString(private val underlying: String) extends AnyVal {
    def withKey()(implicit srv: Credentials): String =
      underlying + s".json?api_version=${srv.version}&app_id=${srv.id}"

    def escaped: String = play.utils.UriEncoding.encodePathSegment(underlying, "UTF-8")
  }
}
