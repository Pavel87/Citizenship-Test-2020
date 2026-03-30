package com.pacmac.citizenship.canada.util

// Utility functions retained for reference; primary logic is now in the data layer.
object Utils {

    fun formatTimeLimit(seconds: Int): String =
        "%02d:%02d".format(seconds / 60, seconds % 60)
}
