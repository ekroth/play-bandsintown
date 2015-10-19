/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth
package bandsintown

/** Commands corresponding to the Bandsintown Web API. */
trait Commands {
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext

  import scalaz._
  import Scalaz._
  import scalaz.contrib._
  import scalaz.contrib.std._

  import play.api.Logger
  import play.api.Application
  import play.api.http.Status._
  import play.api.libs.ws._
  import play.api.libs.json._

  import errorhandling._

  private[bandsintown] def get[T : Reads](query: String)(implicit app: Application, ec: ExecutionContext, srv: Credentials): ResultF[T] =
    Result.okF {
      for {
        resp <- WS.url(query).get()
      } yield {

        println(query)

        resp.json.validate[T] match {
          case JsSuccess(res, _) => res.right
          case e : JsError => {

            /* attempt to read errors */
            resp.json.validate[Errors] match {
              case JsSuccess(res, _) => BandsintownError.Usage(res.errors).left
              case _ : JsError => BandsintownError.Json(e).left
            }
          }
        }
      }
    }


  /*private[bandsintown]*/ def findArtist(name: String)(implicit app: Application, ec: ExecutionContext, srv: Credentials): ResultF[Artist] =
    get[Artist](s"http://api.bandsintown.com/artists/${name.escaped}".withKey())

  def findArtistName(artist: String)(implicit app: Application, ec: ExecutionContext, srv: Credentials): ResultF[Artist] =
    findArtist(artist)


  /*private[bandsintown]*/ def findEvents(artist: String)(implicit app: Application, ec: ExecutionContext, srv: Credentials): ResultF[Seq[Event]] =
    get[Seq[Event]](s"http://api.bandsintown.com/artists/${artist.escaped}/events".withKey())


  /*private[bandsintown]*/ def searchEvents(artist: String, location: String, radius: Int)
    (implicit app: Application, ec: ExecutionContext, srv: Credentials): ResultF[Seq[Event]]
  = get[Seq[Event]](s"http://api.bandsintown.com/artists/${artist.escaped}/events/search".withKey() + s"&location=${location.escaped}&radius=$radius")


  /*private[bandsintown]*/ def searchRecommended(artist: String, location: String, radius: Int, onlyRecs: Boolean)
    (implicit app: Application, ec: ExecutionContext, srv: Credentials): ResultF[Seq[Event]]
  = get[Seq[Event]](s"http://api.bandsintown.com/artists/${artist.escaped}/events/recommended".withKey() + s"&location=${location.escaped}&radius=$radius&only_recs=onlyRecs")

}
