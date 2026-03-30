package com.pacmac.citizenship.canada.util

object Constants {
    const val SUCCESS_ANSWER_COUNT = 15
    const val QUESTION_COUNT = 20

    const val TEST_TIME = 30 * 60
    const val WARN_TIME = 3 * 60

    const val JSON_VERSION = 4

    const val APP_PREFERENCE_FILE = "APP_PREFERENCE_FILE"
    const val LAST_QUESTIONS_VERSION_PREF = "QUESTIONS_VERSION"
    const val SUCCESS_RATE_SUM_PREF = "SUCCESS_RATE_SUM_PREF"
    const val SUCCESS_RATE_COUNT_PREF = "SUCCESS_RATE_COUNT_PREF"
    const val INSIGHT_UNLOCKED_PREF = "INSIGHT_UNLOCKED"

    const val DISCOVER_CANADA_PDF_URL = "https://www.canada.ca/content/dam/ircc/migration/ircc/english/pdf/pub/discover.pdf"

    const val JSON_VERSION_URL = "https://pacmac-cic.firebaseapp.com/json_version.html"
    const val QUESTION_LIST_URL = "https://pacmac-cic.firebaseapp.com/q_a_2020.json"

    const val LATEST_QUESTIONS_FILE = "latestQuestions.json"
}
