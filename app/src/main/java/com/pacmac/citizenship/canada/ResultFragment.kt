package com.pacmac.citizenship.canada

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.util.Constants

class ResultFragment : Fragment() {

    private lateinit var mInterstitialAd: InterstitialAd
    var isClicked: Boolean = false;
    var callback: FragmentSelector? = null

    fun setFragmentSelector(callback: FragmentSelector) {
        this.callback = callback
    }

    companion object {
        fun newInstance() = ResultFragment()
    }

    private lateinit var viewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = getString(R.string.interstitial_id_1)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        viewModel = ViewModelProvider(activity!!).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        view.findViewById<TextView>(R.id.correctAnswers).text = "${viewModel.correctAnswers}"
        if (viewModel.correctAnswers < Constants.SUCCESS_ANSWER_COUNT) {
            view.findViewById<TextView>(R.id.errorMessage).visibility = View.VISIBLE
            view.findViewById<ImageView>(R.id.resultImg).setImageResource(R.drawable.fail_img)
        }

        view.findViewById<ImageView>(R.id.resultImg).visibility = View.VISIBLE

        val loadedQuestionObserver = Observer<List<QuestionObj>> { data ->
            if (data!!.isNotEmpty()) {
                // Change Fragment
                isClicked = false
                callback!!.onStartTest()
            } else {
                viewModel.loadQuestionList(context!!.applicationContext)
            }
        }

        view.findViewById<Button>(R.id.tryAgain).setOnClickListener {
            if (!isClicked) {
                isClicked = true;
                // show progress
                viewModel.getShortQuestionList(context!!.applicationContext)
                    .observe(activity!!, loadedQuestionObserver)
            }
        }

        view.findViewById<Button>(R.id.showAnswerBtn).setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }
            callback!!.onAnswersRequested()
        }
    }

}