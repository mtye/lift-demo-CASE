/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.snippet

import com.marktye.rhcp
import net.liftweb.http._
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

class Giveaway {

  def create(context: NodeSeq): NodeSeq = {
    val giveaway = new model.Giveaway
    def createGiveaway() = {
      giveaway.giver(model.User.currentUser.open_!)
      giveaway.save
      S.notice("Giveaway created!")
      S.redirectTo("/giveaway/list")
    }
    bind("g", context,
      "name" -> giveaway.name.toForm,
      "description" -> giveaway.description.toForm,
      "submit" -> SHtml.submit("Create", createGiveaway)
    )
  }

  def list(context: NodeSeq): NodeSeq = {
    def enterNotice = S.notice("Giveaway entered!")
    def withdrawNotice = S.notice("Giveaway withdrawn from!")
    def giveaways = model.Giveaway.findAll.flatMap { giveaway =>
      bind("g", chooseTemplate("g", "giveaways", context),
           "name" -> giveaway.name,
           "status" -> giveaway.status(enterNotice _, withdrawNotice _),
           "giver" -> giveaway.giver.name("Nobody"),
           "description" -> giveaway.description
      )
    }
    bind("g", context, "giveaways" -> giveaways)
  }
}
