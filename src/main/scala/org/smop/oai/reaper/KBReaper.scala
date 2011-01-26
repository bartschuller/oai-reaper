package org.smop.oai.reaper

import org.smop.oai.HeaderType

class KBReaper extends Reaper("http://services.kb.nl/mdo/oai") {
  def listIdentifiers(metadataPrefix: String, resumptionToken: String): Product2[Iterator[HeaderType], Option[String]] = ListIdentifiersReq.resume(metadataPrefix)(Some(resumptionToken))
}

object KBReaper {
  def main(args: Array[String]) {
    val reaper = new KBReaper
    println(reaper.identify)
  }
}
