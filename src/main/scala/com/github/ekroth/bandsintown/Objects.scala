/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth
package bandsintown

/** Objects corresponding to Bandsintown's object model.
  */
private[bandsintown] trait Objects {

  import scala.collection.immutable.Seq
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  /** Rename json names that are reserved words in Scala.
    *
    * In order to be able to use the Json.writes/reads macro we
    * need to pre- and post-process the JsValues accordingly.
    */
  object TypeNameFix {
    private[this] def replace(in: String, out: String)(json: JsValue): JsValue = json match {
      case x: JsObject => (x \ in) match {
        case y: JsString => (x - in) + (out, y)
        case _ => x
      }
      case x => x
    }

    /** Convert from 'name' to 'keyword'. */
    val afterWrite: JsValue => JsValue = replace("tipe", "type")(_)

    /** Convert from 'keyword' to 'name'. */
    val beforeRead = Reads[JsValue] { js => JsSuccess(replace("type", "tipe")(js)) }
  }

  object Artist {
    implicit val ArtistWrites = Json.writes[Artist].transform(TypeNameFix.afterWrite)
    implicit val ArtistReads = Json.reads[Artist].compose(TypeNameFix.beforeRead)
  }
  case class Artist(name: String, image_url: String, thumb_url: String,
    facebook_tour_dates_url: Option[String], mbid: Option[String],
    upcoming_events_count: Option[Int], tracker_count: Int)

  object Venue {
    implicit val VenueWrites = Json.writes[Venue].transform(TypeNameFix.afterWrite)
    implicit val VenueReads = Json.reads[Venue].compose(TypeNameFix.beforeRead)
  }
  case class Venue(name: String, city: String, region: String, country: String, latitude: Int, longitude: Int)

  object Event {
    implicit val EventWrites = Json.writes[Event].transform(TypeNameFix.afterWrite)
    implicit val EventReads = Json.reads[Event].compose(TypeNameFix.beforeRead)
  }
  case class Event(id: Int, title: String, datetime: String, formatted_datetime: String, formatted_location: String,
    ticket_url: Option[String], ticket_type: Option[String], ticket_status: String, on_sale_datetime: Option[String],
    facebook_rsvp_url: String, description: Option[String], artists: Seq[Artist], venue: Venue)

  object ErrorMessage {
    implicit val ErrorMessageWrites = Json.writes[ErrorMessage].transform(TypeNameFix.afterWrite)
    implicit val ErrorMessageReads = Json.reads[ErrorMessage].compose(TypeNameFix.beforeRead)
  }
  case class ErrorMessage(errors: Seq[String])
}
