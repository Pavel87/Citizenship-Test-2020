package com.pacmac.citizenship.canada

import android.content.res.Configuration
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
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.util.Constants

class ResultFragment : Fragment() {

    private var isClicked: Boolean = false;
    private var callback: FragmentSelector? = null


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
        viewModel = ViewModelProvider(requireActivity()).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        view.findViewById<TextView>(R.id.correctAnswers).text = "${viewModel.correctAnswers}"
        if (viewModel.correctAnswers < Constants.SUCCESS_ANSWER_COUNT) {
            view.findViewById<ImageView>(R.id.resultImg).setImageResource(R.drawable.fail_img)
        }

        view.findViewById<ImageView>(R.id.resultImg).visibility = View.VISIBLE

        val loadedQuestionObserver = Observer<List<QuestionObj>> { data ->
            if (data != null) {

                callback?.onLoadFullScreenAd()
                if (data?.isNotEmpty()) {
                    // Change Fragment
                    isClicked = false
                    callback?.onStartTest()
                } else {
                    viewModel.loadQuestionList(requireContext().applicationContext)
                }
            }
        }

        view.findViewById<Button>(R.id.tryAgain).setOnClickListener {
            if (!isClicked) {
                isClicked = true;
                // show progress
                viewModel.getShortQuestionList(requireContext().applicationContext)
                    .observe(requireActivity(), loadedQuestionObserver)
            }
        }

        view.findViewById<Button>(R.id.showAnswerBtn).setOnClickListener {
            callback?.onAnswersRequested()
        }


        view.findViewById<TextView>(R.id.successRate).text =
            "${viewModel.successRate.value!!.sum / viewModel.successRate.value!!.completedCounter}%"
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
            || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
        ) {
            fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
        }
    }
}
