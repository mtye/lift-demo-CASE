/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.model

import net.liftweb.mapper._
import net.liftweb.http.SHtml

class Giveaway extends LongKeyedMapper[Giveaway] with IdPK with ManyToMany {

  def getSingleton = Giveaway

  object name extends MappedText(this)
  object description extends MappedTextarea(this, 512)
  object giver extends MappedLongForeignKey(this, User) {
    def name(default: String) = obj.dmap(default)(_.shortName)
  }
  object entrants extends MappedManyToMany(Entrant, Entrant.giveaway, Entrant.user, User) {
    def containsCurrentUser = User.currentUser.dmap(false)(this.contains(_))
  }

  def enterCurrentUser() = User.currentUser foreach { user =>
    entrants + user
    entrants.save
  }

  def withdrawCurrentUser() = User.currentUser foreach { user =>
    entrants -= user
    entrants.save
  }

  def status(e: () => Any, w: () => Any) = if (entrants.containsCurrentUser) withdrawButton(w) else enterButton(e)

  def enterButton(f: () => Any) = {
    SHtml.submit("Enter", () => { reload.enterCurrentUser(); f() })
  }

  def withdrawButton(f: () => Any) = {
    SHtml.submit("Withdraw", () => { reload.withdrawCurrentUser(); f() })
  }
}

object Giveaway extends Giveaway with LongKeyedMetaMapper[Giveaway]
