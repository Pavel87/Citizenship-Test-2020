package com.pacmac.citizenship.canada.util

class Constants {

    companion object {

        val INTRO_FRAGMENT = "INTRO"
        val QA_FRAGMENT = "QA_FRAGMENT"
        val RESULT_FRAGMENT = "RESULT_FRAGMENT"
        val ANSWERS_FRAGMENT = "ANSWERS_FRAGMENT"
        val INFO_FRAGMENT = "INFO_FRAGMENT"
        val INSIGHT_FRAGMENT = "INSIGHT_FRAGMENT"

        const val SUCCESS_ANSWER_COUNT = 15
        const val QUESTION_COUNT = 20

        const val TEST_TIME = 30 * 60
        const val WARN_TIME = 3 * 60


        const val JSON_VERSION = 2

        const val APP_PREFERENCE_FILE = "APP_PREFERENCE_FILE"
        const val LAST_QUESTIONS_VERSION_PREF = "QUESTIONS_VERSION"
        const val SUCCESS_RATE_SUM_PREF = "SUCCESS_RATE_SUM_PREF"
        const val SUCCESS_RATE_COUNT_PREF = "SUCCESS_RATE_COUNT_PREF"

        const val JSON_VERSION_URL = "https://pacmac-cic.firebaseapp.com/json_version.html"
        const val QUESTION_LIST_URL = "https://pacmac-cic.firebaseapp.com/q_a_2020.json"

        const val LATEST_QUESTIONS_FILE = "latestQuestions.json"
    }

}