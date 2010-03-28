/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.mapper

import com.marktye.rhcp.text._
import net.liftweb.common.{Box, Full}
import net.liftweb.http.SHtml
import net.liftweb.mapper._
import scala.xml.NodeSeq

abstract class MappedRelativeDate[T <: Mapper[T]](fieldOwner: T) extends MappedDateTime[T](fieldOwner) {

  val defaultUnits = List(Second, Minute, Hour, Day, Week, Month, Year)

  protected def units: List[(UnitOfTime, String)] = defaultUnits.map(u => (u, u.singular))

  protected def epoch = System.currentTimeMillis

  private var quantity = "1"
  private var unit = units.first._1
  private var mode: Mode = Hence

  private def millis = quantity.toLong * unit.millis

  private def setUnderlying() = {
    val date = mode match {
      case Ago => epoch - millis
      case Hence => epoch + millis
    }
    set(new java.util.Date(date))
  }

  private[mapper] def setQuantity(q: String) = {
    quantity = q
    setUnderlying()
  }

  private[mapper] def setUnit(u: UnitOfTime) = {
    unit = u
    setUnderlying()
  }

  private def safeTime = (Box !! is).map(_.getTime)

  def shortFormat = RelativeDateFormat.short.format(safeTime.openOr(0))

  override def _toForm: Box[NodeSeq] = Full(
    SHtml.text(quantity, setQuantity(_)) ++
    SHtml.selectObj(units, Full(unit), setUnit(_: UnitOfTime))
  )

}

abstract sealed case class Mode(name: String)
case object Ago extends Mode("ago")
case object Hence extends Mode("from now")
