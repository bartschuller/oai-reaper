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

  def identify: IdentifyType = {
    val req = baseReq <<? Map(verb -> Identify)
    try {
      http(req <> {
        x =>
          val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
          opts.head.value match {
            case err: OAIPMHerrorType => throw new ReapException(errsToString(opts))
            case myIdentify: IdentifyType => myIdentify
            case other => throw new ReapException("identify failed: got [" + other.toString + "] instead of Identify response")
          }
      })
    } catch {
      case ex: Exception => throw new ReapException("identify failed: ", ex)
    }
  }

  def listMetadataFormats: Seq[MetadataFormatType] = {
    val req = baseReq <<? Map(verb -> ListMetadataFormats)
    try {
      http(req <> {
        x =>
          val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
          opts.head.value match {
            case err: OAIPMHerrorType => throw new ReapException(errsToString(opts))
            case myFormats: ListMetadataFormatsType => myFormats.metadataFormat
            case other => throw new ReapException("listMetadataFormats failed: got [" + other.toString + "] instead of ListMetadataFormats response")
          }
      })
    } catch {
      case ex: Exception => throw new ReapException("listMetadataFormats failed: ", ex)
    }
  }

  protected class ResumptionIterator[T](initialSeq: Seq[T], resume: (Option[String]) => Product2[Iterator[T], Option[String]], var token: Option[String]) extends Iterator[T] {
    var innerIterator = initialSeq.iterator

    def next(): T = innerIterator.next()

    def hasNext =
      if (innerIterator.hasNext)
        true
      else {
        val (newIterator, newToken) = resume(token)
        innerIterator = newIterator
        token = newToken
        innerIterator.hasNext
      }
  }

  protected object ListSetsReq {
    def initial(): Iterator[SetType] = {
      val (set, token) = doReq(baseReq <<? Map(verb -> ListSets))
      new ResumptionIterator(set, resume _, token)
    }

    def resume(resumptionToken: Option[String]): Product2[Iterator[SetType], Option[String]] = {
      resumptionToken match {
        case None => (Iterator(), None)
        case Some(token) => {
          val (seq, newToken) = doReq(baseReq <<? Map(verb -> ListSets, "resumptionToken" -> token))
          return (seq.iterator, newToken)
        }
      }
    }

    def doReq(req: Request): Product2[Seq[SetType], Option[String]] =
      try {
        http(req <> {
          x =>
            val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
            opts.head.value match {
              case err: OAIPMHerrorType => throw new ReapException(errsToString(opts))
              case mySets: ListSetsType => (mySets.set, mySets.resumptionToken.map(_.value))
              case other => throw new ReapException("listSets failed: got [" + other.toString + "] instead of ListSets response")
            }
        })
      } catch {
        case ex: Exception => throw new ReapException("listSets failed: ", ex)
      }
  }

  def listSets: Iterator[SetType] = ListSetsReq.initial

  protected object ListIdentifiersReq {
    def initial(metadataPrefix: String, set: Option[String], from: Option[String], until: Option[String]): Iterator[HeaderType] = {
      val params: Map[String, String] = Map(verb -> ListIdentifiers.toString, "metadataPrefix" -> metadataPrefix, "set" -> set, "from" -> from, "until" -> until).filter(_ match {
        case (_, None) => false
        case _ => true
      }).mapValues(_ match {
        case Some(v: String) => v
        case v: String => v
      })
      val (seq, token) = doReq(baseReq <<? params)
      new ResumptionIterator(seq, resume(metadataPrefix) _, token)
    }

    def resume(metadataPrefix: String)(resumptionToken: Option[String]): Product2[Iterator[HeaderType], Option[String]] = {
      resumptionToken match {
        case None => (Iterator(), None)
        case Some("") => (Iterator(), None)
        case Some(token) => {
          val (set, newToken) = doReq(baseReq <<? Map(verb -> ListIdentifiers, "metadataPrefix" -> metadataPrefix, "resumptionToken" -> token))
          return (set.iterator, newToken)
        }
      }
    }

    def doReq(req: Request): Product2[Seq[HeaderType], Option[String]] =
      try {
        http(req <> {
          x =>
            val opts = fromXML[OAIPMHtype](x).oaipmhtypeoption
            opts.head.value match {
              case err: OAIPMHerrorType => throw new ReapException(errsToString(opts))
              case idents: ListIdentifiersType => (idents.header, idents.resumptionToken.map(_.value))
              case other => throw new ReapException("listIdentifiers failed: got [" + other.toString + "] instead of ListIdentifiers response")
            }
        })
      } catch {
        case ex: Exception => throw new ReapException("listIdentifiers failed: ", ex)
      }
  }

  def listIdentifiers(metadataPrefix: String, set: Option[String] = None, from: Option[String] = None, until: Option[String] = None): Iterator[HeaderType] = ListIdentifiersReq.initial(metadataPrefix, set, from, until)

  protected def errsToString(errs: Seq[DataRecord[OAIPMHtypeOption]]): String =
    errs.foldLeft("") {
      (b, a) => b + a.as[OAIPMHerrorType].toString + "\n"
    }
}

object Reaper {
  def apply(baseUrl: String) = new Reaper(baseUrl)

  val verb = "verb"
}

class ReapException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause)
