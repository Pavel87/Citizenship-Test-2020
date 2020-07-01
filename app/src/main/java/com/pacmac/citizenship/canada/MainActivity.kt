package com.pacmac.citizenship.canada

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import com.pacmac.citizenship.canada.util.Constants

class MainActivity : AppCompatActivity(), FragmentSelector {

    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(applicationContext)

        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)
        viewModel.loadQuestionList(applicationContext)

//        Thread(Runnable {
        viewModel.downloadLatestQuestions(applicationContext)
//        }).start()

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
        }
    }

    override fun onStartTest() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, QAFragment.newInstance(), Constants.QA_FRAGMENT)
            .commit()
    }

    override fun onTestComplete() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, ResultFragment.newInstance(), Constants.RESULT_FRAGMENT)
            .commit()
    }

    override fun onAnswersRequested() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment, AnswersFragment.newInstance(), Constants.ANSWERS_FRAGMENT)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(Constants.INTRO_FRAGMENT) != null) {
            super.onBackPressed()
        } else if (supportFragmentManager.findFragmentByTag(Constants.ANSWERS_FRAGMENT) != null) {
            onTestComplete()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, IntroFragment.newInstance(), Constants.INTRO_FRAGMENT)
                .commit()
        }
    }


}