package com.pacmac.citizenship.canada

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.pacmac.citizenship.canada.util.Constants


class MainActivity : AppCompatActivity(), FragmentSelector {

    private lateinit var viewModel: AppViewModel
    private lateinit var adsHelper: AdsHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsHelper = AdsHelper(context = this)

        MobileAds.initialize(applicationContext) {
            adsHelper.initializeFullScreenAd()
            adsHelper.initializeRewardedAd()
        }

        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        viewModel.loadQuestionList(applicationContext)
        viewModel.downloadLatestQuestions(applicationContext)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment, IntroFragment.newInstance(), Constants.INTRO_FRAGMENT)
                .commit()
        }
    }


    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is IntroFragment) {
            fragment.setFragmentSelector(this)
        } else if (fragment is QAFragment) {
            fragment.setFragmentSelector(this)
        } else if (fragment is ResultFragment) {
            fragment.setFragmentSelector(this)
        } else if (fragment is InfoFragment) {
            fragment.setFragmentSelector(this)
        }
    }

    override fun onLoadFullScreenAd() {
        try {
            adsHelper.initializeFullScreenAd()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRewardedAdRequested(callback: (isUnlocked: Boolean)->Unit) {
        adsHelper.showRewardedApp(this) { isUnlocked ->
            callback.invoke(isUnlocked)
        }
    }

    override fun onStartTest() {
        viewModel.correctAnswers = 0
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, QAFragment.newInstance(), Constants.QA_FRAGMENT)
            .commit()
    }

    override fun onTestComplete(updateSuccessRate: Boolean) {

        // Update Success Rate
        if (updateSuccessRate && viewModel.successRate.value != null) {
            viewModel.successRate.value!!.sum += 100 * viewModel.correctAnswers / Constants.QUESTION_COUNT
            viewModel.successRate.value!!.completedCounter++
            viewModel.updateSuccessRate(applicationContext)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, ResultFragment.newInstance(), Constants.RESULT_FRAGMENT)
            .commit()
    }

    override fun onAnswersRequested() {
        adsHelper.showInterestialAd(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, AnswersFragment.newInstance(), Constants.ANSWERS_FRAGMENT)
            .commit()
    }

    override fun onInfoRequested() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, InfoFragment.newInstance(), Constants.INFO_FRAGMENT)
            .commit()
    }

    override fun onInsightFragmentRequested() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, InsightFragment.newInstance(), Constants.INSIGHT_FRAGMENT)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(Constants.INTRO_FRAGMENT) != null) {
            super.onBackPressed()
        } else if (supportFragmentManager.findFragmentByTag(Constants.ANSWERS_FRAGMENT) != null) {
            onTestComplete(false)
        } else if (supportFragmentManager.findFragmentByTag(Constants.INSIGHT_FRAGMENT) != null) {
            onInfoRequested()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, IntroFragment.newInstance(), Constants.INTRO_FRAGMENT)
                .commit()
        }
    }
}