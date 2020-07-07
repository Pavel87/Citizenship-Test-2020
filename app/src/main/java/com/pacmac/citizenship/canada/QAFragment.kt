package com.pacmac.citizenship.canada

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pacmac.citizenship.canada.model.Answer
import com.pacmac.citizenship.canada.model.QuestionObj
import com.pacmac.citizenship.canada.util.Constants
import com.pacmac.citizenship.canada.util.Utils

/**
 * A simple [Fragment] subclass.
 * Use the [QAFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QAFragment : Fragment() {

    companion object {
        fun newInstance() = QAFragment()
    }

    var callback: FragmentSelector? = null

    val mainHandler = Handler(Looper.getMainLooper())
    var time = Constants.TEST_TIME
    val WARN_TIME = Constants.WARN_TIME
    private lateinit var timerRunnable: Runnable

    var exitTime: Long = -1;


    fun setFragmentSelector(callback: FragmentSelector) {
        this.callback = callback
    }

    private lateinit var viewModel: AppViewModel
    private lateinit var submitButton: Button
    private var questionNumber = 1

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        retainInstance = true
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(activity!!).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_q_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Utils.showBannerAdView(view.findViewById(R.id.adContainer), context!!, R.string.banner_id_1)

        showQuestion(view, viewModel.getQuestionList()?.get(questionNumber - 1), questionNumber)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        submitButton = view.findViewById(R.id.submitButton)

        // if rotated
        if (questionNumber == Constants.QUESTION_COUNT) {
            submitButton.text = "Submit"
        }
        submitButton.setOnClickListener {
            val correctAnswer = getAnswer(radioGroup.checkedRadioButtonId)

            if (correctAnswer == Answer.UNKNOWN) {
                return@setOnClickListener
            }

            radioGroup.clearCheck()
            viewModel.getQuestionList()?.get(questionNumber - 1)?.userAnswer = correctAnswer
            if (viewModel.getQuestionList()?.get(questionNumber - 1)?.isCorrectAnswer()!!) {
                viewModel.correctAnswers++
            }

            if (questionNumber < Constants.QUESTION_COUNT) {
                questionNumber++

                showQuestion(
                    view,
                    viewModel.getQuestionList()?.get(questionNumber - 1),
                    questionNumber
                )

            } else {
                callback!!.onTestComplete()
            }

            if (questionNumber == Constants.QUESTION_COUNT) {
                submitButton.text = "Submit"
            }
        }


        timerRunnable = Runnable {
            view.findViewById<TextView>(R.id.timer).text = Utils.formatTimeLimit(time)
            if (time < WARN_TIME) {
                view.findViewById<View>(R.id.timerHighlighter)
                    .setBackgroundColor(context!!.resources.getColor(R.color.bad_time))
            }

            if (time > 0) {
                reduceTime()
                mainHandler.postDelayed(timerRunnable, 1000)
            } else {
                val correctAnswer = getAnswer(radioGroup.checkedRadioButtonId)
                viewModel.getQuestionList()?.get(questionNumber - 1)?.userAnswer = correctAnswer
                callback!!.onTestComplete()
            }
        }
    }

    fun reduceTime() {
        time--
    }


    fun showQuestion(view: View, questionObj: QuestionObj?, questionNumber: Int) {
        view.findViewById<TextView>(R.id.questionNumber).text = "$questionNumber"
        view.findViewById<TextView>(R.id.question).text = questionObj?.question
        view.findViewById<RadioButton>(R.id.answer1).text = questionObj?.a
        view.findViewById<RadioButton>(R.id.answer2).text = questionObj?.b
        view.findViewById<RadioButton>(R.id.answer3).text = questionObj?.c
        view.findViewById<RadioButton>(R.id.answer4).text = questionObj?.d
    }


    fun getAnswer(id: Int): Answer {
        when (id) {
            R.id.answer1 -> return Answer.ANSWER_A
            R.id.answer2 -> return Answer.ANSWER_B
            R.id.answer3 -> return Answer.ANSWER_C
            R.id.answer4 -> return Answer.ANSWER_D
        }
        return Answer.UNKNOWN
    }


    override fun onResume() {
        super.onResume()
        if (exitTime > 0) {
            val diff = (SystemClock.elapsedRealtime() - exitTime).toInt() / 1000
            if ((time - diff) > 0 && (time - diff) < Constants.TEST_TIME) {
                time -= diff
            } else if (time - diff < 1) {
                time = 1
            }
            exitTime = -1
        }
        mainHandler.post(timerRunnable)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(timerRunnable)
        exitTime = SystemClock.elapsedRealtime()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
        }
    }
}