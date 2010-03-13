/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.model

import net.liftweb.mapper._

class Giveaway extends LongKeyedMapper[Giveaway] with IdPK {

  def getSingleton = Giveaway

  object name extends MappedText(this)
  object description extends MappedTextarea(this, 512)
}

object Giveaway extends Giveaway with LongKeyedMetaMapper[Giveaway]
