package com.pacmac.citizenship.canada

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.pacmac.citizenship.canada.util.Constants
import java.lang.Exception

class MainActivity : AppCompatActivity(), FragmentSelector {

    private lateinit var viewModel: AppViewModel
    private var mInterstitialAd: InterstitialAd? = null
    private var adShown = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(applicationContext) {
            initializeFullScreenAd()
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

    fun initializeFullScreenAd() {
        mInterstitialAd = InterstitialAd(applicationContext)
        mInterstitialAd?.adUnitId = getString(R.string.interstitial_id_1)
        mInterstitialAd?.loadAd(AdRequest.Builder().build())
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is IntroFragment) {
            fragment.setFragmentSelector(this)
        } else if (fragment is QAFragment) {
            fragment.setFragmentSelector(this)
        } else if (fragment is ResultFragment) {
            fragment.setFragmentSelector(this)
        }
    }

    override fun onLoadFullScreenAd() {
        try {
            if (mInterstitialAd == null) {
                initializeFullScreenAd()
            }
            mInterstitialAd?.loadAd(AdRequest.Builder().build())
        } catch (e: Exception) {
            e.printStackTrace()
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
        if (mInterstitialAd?.isLoaded ?: false && !adShown) {
            mInterstitialAd?.show()
        } else {
            onLoadFullScreenAd()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, AnswersFragment.newInstance(), Constants.ANSWERS_FRAGMENT)
            .commit()
    }

    override fun onInfoRequested() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, InfoFragment.newInstance(), Constants.INFO_FRAGMENT)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(Constants.INTRO_FRAGMENT) != null) {
            super.onBackPressed()
        } else if (supportFragmentManager.findFragmentByTag(Constants.ANSWERS_FRAGMENT) != null) {
            onTestComplete(false)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, IntroFragment.newInstance(), Constants.INTRO_FRAGMENT)
                .commit()
        }
    }


}