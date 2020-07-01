package com.pacmac.citizenship.canada.util

class Constants {

    companion object {

        val INTRO_FRAGMENT = "INTRO"
        val QA_FRAGMENT = "QA_FRAGMENT"
        val RESULT_FRAGMENT = "RESULT_FRAGMENT"
        val ANSWERS_FRAGMENT = "ANSWERS_FRAGMENT"

        const val SUCCESS_ANSWER_COUNT = 15

        const val TEST_TIME = 30 * 60


        const val JSON_VERSION = 0

        const val APP_PREFERENCE_FILE = "APP_PREFERENCE_FILE"
        const val LAST_QUESTIONS_VERSION_PREF = "QUESTIONS_VERSION"
//        const val LAST_QUESTIONS_JSON_FILE_PREF = "QUESTIONS_JSON_FILE"

        const val JSON_VERSION_URL = "https://pacmac-cic.firebaseapp.com/json_version.html"
        const val QUESTION_LIST_URL = "https://pacmac-cic.firebaseapp.com/q_a_2020.json"

        const val LATEST_QUESTIONS_FILE = "latestQuestions.json"
    }

}