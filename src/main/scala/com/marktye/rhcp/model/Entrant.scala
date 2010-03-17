/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.model

import net.liftweb.mapper._

class Entrant extends LongKeyedMapper[Entrant] with IdPK {

  def getSingleton = Entrant

  object giveaway extends MappedLongForeignKey(this, Giveaway) {
    override def dbIndexed_? = true
  }

  object user extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }
}

object Entrant extends Entrant with LongKeyedMetaMapper[Entrant] {
  override def dbIndexes = UniqueIndex(user, giveaway) :: super.dbIndexes
}
