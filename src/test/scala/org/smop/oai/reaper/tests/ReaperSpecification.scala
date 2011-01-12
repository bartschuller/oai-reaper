package org.smop.oai.reaper.tests

import org.specs._
import log.ConsoleLog
import org.smop.oai.reaper.Reaper
import java.net.URI
import org.smop.oai._

/**
 * Created by IntelliJ IDEA.
 * User: schuller
 * Date: 1/10/11
 * Time: 20:50
 * To change this template use File | Settings | File Templates.
 */

object ReaperSpecification extends Specification with ConsoleLog {
  "The Reaper" should {
    val reaper = new Reaper("http://www.archive.org/services/oai2.php")
    "handle Identify" in {
      reaper.identify match {
        case Right(id: IdentifyType) =>
          id.adminEmail must containMatchOnlyOnce("info@archive.org")
        case Left(wrong) => fail("got "+wrong)
      }
    }
    "handle errors" in {
      val errorReaper = Reaper("http://www.archive.org/services/oai2not.php")
      errorReaper.identify match {
        case Left(message) => message must notBeEmpty
        case Right(wrong) => fail("got "+wrong)
      }
    }
    "handle ListMetadataFormats" in {
      reaper.listMetadataFormats match {
        case Right(formats: Seq[_]) =>
          formats must contain(MetadataFormatType("oai_dc", new URI("http://www.openarchives.org/OAI/2.0/oai_dc.xsd"), new URI("http://www.openarchives.org/OAI/2.0/oai_dc/")))
        case Left(wrong) => fail("got "+wrong)
      }
    }
    "handle ListSets" in {
      reaper.listSets match {
        case Right(formats: Seq[_]) =>
          formats must contain(SetType("collection:RidingShotgun","Items with collection equal to RidingShotgun",List()))
        case Left(wrong) => fail("got "+wrong)
      }
    }
    "handle ListIdentifiers" in {
      reaper.listIdentifiers("oai_dc", Some("collection:RidingShotgun")) match {
        case Right(headers: Seq[_]) =>
          headers must contain(HeaderType(new URI("oai:archive.org:ridingshotgun2006-03-25.sbd.flac16"), "2010-01-31T06:08:34Z", List("mediatype:etree", "collection:RidingShotgun", "collection:etree")))
        case Left(wrong) => fail("got "+wrong)
      }
    }
  }
}
