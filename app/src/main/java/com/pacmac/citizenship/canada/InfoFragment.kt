package com.pacmac.citizenship.canada

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pacmac.citizenship.canada.model.SuccessRate
import com.pacmac.citizenship.canada.util.Utils


class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment()
    }

    private lateinit var viewModel: AppViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(activity!!).get(AppViewModel::class.java)
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        view.findViewById<TextView>(R.id.versionString).text =
                context!!.packageManager.getPackageInfo(context!!.packageName, 0).versionName
        view.findViewById<TextView>(R.id.jsonVersion).text = "${Utils.latestJSONVersion}"
        view.findViewById<TextView>(R.id.qCounter).text =
                "${viewModel.allQuestionList.value!!.size}"


        viewModel.getLastSuccessRate(context!!)
        val successRateObserver = Observer<SuccessRate> { data ->

            if (data.completedCounter != 0) {
                view.findViewById<TextView>(R.id.successRate).text = "${data.sum / data.completedCounter}%"
            } else {
                view.findViewById<TextView>(R.id.successRate).text = "0%"
            }
        }
        viewModel.successRate.observe(activity!!, successRateObserver)


        view.findViewById<Button>(R.id.submitRating).setOnClickListener {
            val appPackage = context!!.packageName
            val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")
            )
            context!!.startActivity(intent)
        }
    }

}