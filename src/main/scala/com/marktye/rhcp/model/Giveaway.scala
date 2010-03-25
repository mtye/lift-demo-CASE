/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.model

import net.liftweb.mapper._
import net.liftweb.http.SHtml
import scala.util.Random

class Giveaway extends LongKeyedMapper[Giveaway] with IdPK with ManyToMany {

  def getSingleton = Giveaway

  object name extends MappedText(this)
  object description extends MappedTextarea(this, 512)
  object giver extends MappedLongForeignKey(this, User) {
    def name(default: String) = obj.dmap(default)(_.shortName)
    def isCurrentUser = User.currentUser.dmap(false)(this == _)
  }
  object entrants extends MappedManyToMany(Entrant, Entrant.giveaway, Entrant.user, User) {
    def containsCurrentUser = User.currentUser.dmap(false)(this.contains(_))
    def random = if (isEmpty) None else Some(this(Giveaway.random.nextInt(length)))
  }
  object winner extends MappedLongForeignKey(this, User) {
    def name(default: String) = obj.dmap(default)(_.shortName)
    def isCurrentUser = User.currentUser.dmap(false)(this == _)
    def notChosenYet = ! defined_?
    def choose() = {
      if (notChosenYet) apply(fieldOwner.randomEntrant).save
      this
    }
  }

  def enterCurrentUser() = if (winner.notChosenYet) User.currentUser foreach { user =>
    entrants + user
    entrants.save
  }

  def withdrawCurrentUser() = if (winner.notChosenYet) User.currentUser foreach { user =>
    entrants -= user
    entrants.save
  }

  def randomEntrant = entrants.random getOrElse (giver.obj.open_!)

  def status(e: () => Any, w: () => Any) = (winner.notChosenYet, entrants.containsCurrentUser, winner.isCurrentUser) match {
    case (true, true, _) => withdrawButton(w)
    case (true, false, _) => enterButton(e)
    case (false, true, true) => <span>Won</span>
    case (false, true, false) => <span>Lost</span>
    case (false, false, _) => <span>Not Entered</span>
  }

  def enterButton(f: () => Any) = {
    SHtml.submit("Enter", () => { reload.enterCurrentUser(); f() })
  }

  def withdrawButton(f: () => Any) = {
    SHtml.submit("Withdraw", () => { reload.withdrawCurrentUser(); f() })
  }

  def link = <a href={"detail/" + id.is}>{name}</a>

  def chooseWinner(f: () => Any) = if (winner.notChosenYet && giver.isCurrentUser) chooseWinnerButton(f)
                                   else <span>{winner.name("\u00A0")}</span>

  def chooseWinnerButton(f: () => Any) = {
    SHtml.submit("Choose Winner", () => { reload.winner.choose(); f() })
  }

}

object Giveaway extends Giveaway with LongKeyedMetaMapper[Giveaway] {

  val random = new Random(System.currentTimeMillis)
}
