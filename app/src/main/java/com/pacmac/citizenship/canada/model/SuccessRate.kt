package com.pacmac.citizenship.canada.model

data class SuccessRate(val sum: Int, val completedCounter: Int) {
    val averagePercent: Int get() = if (completedCounter == 0) 0 else sum / completedCounter
}
