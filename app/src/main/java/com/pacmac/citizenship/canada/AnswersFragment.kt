package com.pacmac.citizenship.canada

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pacmac.citizenship.canada.util.Constants

/**
 * A fragment representing a list of Items.
 */
class AnswersFragment : Fragment() {

    private lateinit var viewModel: AppViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(activity!!).get(AppViewModel::class.java)
        container!!.removeAllViews()
        val view = inflater.inflate(R.layout.fragment_answers_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val list = view.findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = AnswerRecyclerViewAdapter(viewModel.getQuestionList()!!)
        list.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        view.findViewById<TextView>(R.id.correctAnswers).text = "${viewModel.correctAnswers}"

        if (viewModel.correctAnswers < Constants.SUCCESS_ANSWER_COUNT) {
            view.findViewById<LinearLayout>(R.id.resultBg).setBackgroundResource(R.color.fail_bg)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AnswersFragment()
    }
}