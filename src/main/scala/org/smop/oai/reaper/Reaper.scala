package org.smop.oai.reaper

import dispatch._
import scalaxb._
import Scalaxb._
import org.smop.oai.DefaultXMLProtocol._
import org.smop.oai._

/**
 * Created by IntelliJ IDEA.
 * User: schuller
 * Date: 1/10/11
 * Time: 20:55
 * To change this template use File | Settings | File Templates.
 */

class Reaper(baseUrl: String) {

  import Reaper.verb

  val http = new Http()
  val baseReq = new Request(baseUrl)

  def identify: Either[String, IdentifyType] = {
    val req = baseReq <<? Map(verb -> Identify)
    try {
      http(req <> {
        x =>
          val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
          opts.head.value match {
            case err: OAIPMHerrorType => Left(errsToString(opts))
            case myIdentify: IdentifyType => Right(myIdentify)
            case other => Left("got [" + other.toString + "] instead of Identify response")
          }
      })
    } catch {
      case ex => Left(ex.toString)
    }
  }

  def listMetadataFormats: Either[String, Seq[MetadataFormatType]] = {
    val req = baseReq <<? Map(verb -> ListMetadataFormats)
    try {
      http(req <> {
        x =>
          val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
          opts.head.value match {
            case err: OAIPMHerrorType => Left(errsToString(opts))
            case myFormats: ListMetadataFormatsType => Right(myFormats.metadataFormat)
            case other => Left("got [" + other.toString + "] instead of Identify response")
          }
      })
    } catch {
      case ex => Left(ex.toString)
    }
  }

  def listSets: Either[String, Seq[SetType]] = {
    val req = baseReq <<? Map(verb -> ListSets)
    try {
      http(req <> {
        x =>
          val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
          opts.head.value match {
            case err: OAIPMHerrorType => Left(errsToString(opts))
            case mySets: ListSetsType => Right(mySets.set) // TODO handle resumption
            case other => Left("got [" + other.toString + "] instead of Identify response")
          }
      })
    } catch {
      case ex => Left(ex.toString)
    }
  }

  private def errsToString(errs: Seq[DataRecord[OAIPMHtypeOption]]): String =
    errs.foldLeft("") {
      (b, a) => b + a.as[OAIPMHerrorType].toString + "\n"
    }
}

object Reaper {
  def apply(baseUrl: String) = new Reaper(baseUrl)

  val verb = "verb"
}
