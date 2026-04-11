package com.ashaworker.triage.util

import java.util.Calendar
import java.util.Locale

object DateUtils {

    fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }

    fun formatTimestamp(ts: Long): String {
        val date = java.util.Date(ts)
        return java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
    }
}
