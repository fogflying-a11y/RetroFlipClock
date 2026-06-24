package com.retroflip.clock.data

import java.util.Calendar

object LunarCalendar {

    private const val GAN = "甲乙丙丁戊己庚辛壬癸"
    private const val ZHI = "子丑寅卯辰巳午未申酉戌亥"
    private const val ZODIAC = "鼠牛虎兔龙蛇马羊猴鸡狗猪"

    private val MONTH_NAMES = arrayOf(
        "", "正月", "二月", "三月", "四月", "五月", "六月",
        "七月", "八月", "九月", "十月", "冬月", "腊月"
    )

    private val DAY_NAMES = arrayOf(
        "", "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
        "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
        "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
    )

    fun toLunar(calendar: Calendar): LunarDate {
        val libLunar = com.nlf.calendar.Lunar.fromDate(calendar.time)
        val rawMonth = libLunar.month
        return LunarDate(
            year = libLunar.year,
            month = kotlin.math.abs(rawMonth),
            day = libLunar.day,
            isLeapMonth = rawMonth < 0
        )
    }

    fun getGanZhiYear(lunarYear: Int): String {
        val ganIndex = (lunarYear - 4) % 10
        val zhiIndex = (lunarYear - 4) % 12
        return "${GAN[ganIndex]}${ZHI[zhiIndex]}年"
    }

    fun getZodiac(lunarYear: Int): String {
        val zhiIndex = (lunarYear - 4) % 12
        return "${ZODIAC[zhiIndex]}年"
    }

    fun getLunarMonthName(month: Int, isLeap: Boolean): String {
        val prefix = if (isLeap) "闰" else ""
        return "$prefix${MONTH_NAMES[month]}"
    }

    fun getLunarDayName(day: Int): String =
        if (day in 1..30) DAY_NAMES[day] else "初一"

    fun formatSolar(calendar: Calendar): String {
        val y = calendar.get(Calendar.YEAR)
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DATE)
        return "${y}年${m.toString().padStart(2, '0')}月${d.toString().padStart(2, '0')}日"
    }

    fun getHuangli(calendar: Calendar): Pair<String, String> {
        val libLunar = com.nlf.calendar.Lunar.fromDate(calendar.time)
        val yi = try {
            libLunar.dayYi.take(3).joinToString(" ")
        } catch (_: Exception) { "" }
        val ji = try {
            libLunar.dayJi.take(3).joinToString(" ")
        } catch (_: Exception) { "" }
        return Pair(yi, ji)
    }

    data class LunarDate(
        val year: Int,
        val month: Int,
        val day: Int,
        val isLeapMonth: Boolean = false
    )
}
