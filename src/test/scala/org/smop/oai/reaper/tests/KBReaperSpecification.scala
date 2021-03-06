package org.smop.oai.reaper.tests

import java.net.URI
import org.specs.Specification
import org.specs.log.ConsoleLog
import org.smop.oai._
import reaper.KBReaper
import xml.NodeSeq


class KBReaperSpecification extends Specification with ConsoleLog {
  "The Koninklijke Bibibliotheek Reaper" should {
    val reaper = new KBReaper()
    "handle Identify" in {
      reaper.identify.adminEmail must containMatchOnlyOnce("theo.vanveen@kb.nl")
    }
    "handle ListMetadataFormats" in {
      reaper.listMetadataFormats.map(_.metadataPrefix) must contain("didl")
    }
    "handle ListIdentifiers" in {
      val l = reaper.listIdentifiers("didl", Some("anp")).take(2000).length
      l must be_==(2000)
    }
    "handle ListRecords" in {
      val l = reaper.listRecords("didl", Some("anp")).take(500).length
      l must be_==(500)
    }
    /*
    "handle bogus ResumptionToken" in {
      skip("test data changed from under us")
      val (iter, newToken) = reaper.listIdentifiers("didl", "SGD!2010-11-08T12:41:35.702Z!null!didl!455200")
      val idents = iter.toSeq.map(_.identifier.toString)
      idents must contain("SGD:sgd:mpeg21:19781979:0007057")
    }
    */
    "handle getRecord" in {
      reaper.getRecord("anp:anp:1937:10:07:22:mpeg21", "didl").metadata.get.any.value.asInstanceOf[NodeSeq] must \\(<recordRights/>)
    }
  }
}
