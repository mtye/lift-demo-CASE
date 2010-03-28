/* Copyright © 2010 Mark Tye
 *
 * This work is licensed under the Creative Commons Attribution-Noncommercial-
 * Share Alike 3.0 United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/
 */
package com.marktye.rhcp.text

import java.text.DateFormat
import java.text.FieldPosition
import java.text.ParsePosition
import java.util.Date

class RelativeDateFormat(style: Format, epoch: Long, units: List[UnitOfTime]) extends DateFormat {

  lazy val sortedUnits = units.sort(_.millis > _.millis)

  override def parse(source: String, pos: ParsePosition): Date =
    throw new UnsupportedOperationException

  override def format(date: Date, toAppendTo: StringBuffer, fieldPosition: FieldPosition): StringBuffer = {
    val millis = date.getTime - epoch
    val times = partition(millis).remove(_.amt == 0)
    times.take(style.howManyUnits).foreach { time =>
        toAppendTo.append(time.amtString).append(" ")
        toAppendTo.append(time.unitString).append(", ")
    }
    if (times.isEmpty)
      toAppendTo.append("0 ").append(sortedUnits.last.plural)
    else
      toAppendTo.delete(toAppendTo.length -2, toAppendTo.length)
    toAppendTo.append(if (millis > 0) " from now" else " ago")
  }

  def partition(millis: Long): List[Time] = partition(millis, sortedUnits)

  protected def partition(millis: Long, units: List[UnitOfTime]): List[Time] = units match {
    case Nil => Nil
    case unit :: units => Time(millis / unit.millis, unit) :: partition(millis % unit.millis, units)
  }
}

object RelativeDateFormat {

  def epoch = System.currentTimeMillis
  val defaultFormat = List(Year, Month, Week, Day, Hour, Minute, Second)

  def short = new RelativeDateFormat(Short, epoch, defaultFormat)
  def medium = new RelativeDateFormat(Medium, epoch, defaultFormat)
  def long = new RelativeDateFormat(Long, epoch, defaultFormat)
  def full = new RelativeDateFormat(Full, epoch, defaultFormat)
}

abstract case class Format(howManyUnits: Int)
case object Short extends Format(1)
case object Medium extends Format(2)
case object Long extends Format(3)
case object Full extends Format(Int.MaxValue)

abstract case class UnitOfTime(millis: Long, singular: String) {
  val plural = singular + "s"
  override def toString = singular
}
case object Millisecond extends UnitOfTime(          1L, "millisecond")
case object Second      extends UnitOfTime(       1000L, "second")
case object Minute      extends UnitOfTime(      60000L, "minute")
case object Hour        extends UnitOfTime(    3600000L, "hour")
case object Day         extends UnitOfTime(   86400000L, "day")
case object Week        extends UnitOfTime(  604800000L, "week")
case object Month       extends UnitOfTime( 2629800000L, "month")
case object Year        extends UnitOfTime(31557600000L, "year")

case class Time(amt: Long, unit: UnitOfTime) {
  def amtString = amt.abs.toString
  def unitString = if (amt.abs == 1) unit.singular else unit.plural
}
