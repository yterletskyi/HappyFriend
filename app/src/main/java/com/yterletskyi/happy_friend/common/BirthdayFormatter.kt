package com.yterletskyi.happy_friend.common

import com.yterletskyi.happy_friend.common.x.hasYear
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

interface BirthdayFormatter {
    fun format(birthday: LocalDate?): String
}

class DevBirthdayFormatter : BirthdayFormatter {

    override fun format(birthday: LocalDate?): String = birthday?.run {
        when {
            hasYear -> "$year-$month-$dayOfMonth"
            else -> "$month-$dayOfMonth"
        }
    }.orEmpty()
}

class LocalizedBirthdayFormatter(
    private val locale: Locale
) : BirthdayFormatter {

    override fun format(birthday: LocalDate?): String = birthday?.run {
        val format = when {
            hasYear -> DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
            else -> DateTimeFormatter.ofPattern("dd MMM", locale)
        }
        format.format(birthday)
    }.orEmpty()
}