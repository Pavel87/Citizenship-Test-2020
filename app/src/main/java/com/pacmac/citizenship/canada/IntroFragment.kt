package com.pacmac.citizenship.canada

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pacmac.citizenship.canada.model.QuestionObj

class IntroFragment : Fragment() {

    var isClicked: Boolean = false;
    var callback: FragmentSelector? = null


    fun setFragmentSelector(callback: FragmentSelector) {
        this.callback = callback
    }

    companion object {
        fun newInstance() = IntroFragment()
    }

    private lateinit var viewModel: AppViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.intro_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(activity!!).get(AppViewModel::class.java)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val loadedQuestionObserver = Observer<List<QuestionObj>> { data ->
            callback!!.onLoadFullScreenAd()
            if (data!!.isNotEmpty()) {
                // Change Fragment
                view.findViewById<ProgressBar>(R.id.startProgress).visibility = View.INVISIBLE
                isClicked = false
                callback!!.onStartTest()
            } else {
                viewModel.loadQuestionList(context!!.applicationContext)
            }
        }

        val startTestBtn = view.findViewById<Button>(R.id.startTest)
        startTestBtn.setOnClickListener {

            if (!isClicked) {
                isClicked = true;
                // show progress
                view.findViewById<ProgressBar>(R.id.startProgress).visibility = View.VISIBLE
                viewModel.getShortQuestionList(context!!.applicationContext)
                        .observe(activity!!, loadedQuestionObserver)

            }
        }

        view.findViewById<ImageView>(R.id.about).setOnClickListener {
            callback!!.onInfoRequested()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentManager!!.beginTransaction().detach(this).attach(this).commit()
        }
    }

}