/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.snippet

import net.liftweb.http._
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

class Giveaway {

  def create(context: NodeSeq): NodeSeq = {
    var name = ""
    var description = ""
    def createGiveaway() = {
      S.notice("Giveaway created!")
      S.redirectTo("/index")
    }
    bind("g", context,
      "name" -> SHtml.text(name, name = _),
      "description" -> SHtml.textarea(description, description = _),
      "submit" -> SHtml.submit("Create", createGiveaway)
    )
  }
}
