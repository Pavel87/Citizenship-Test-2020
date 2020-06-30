package com.pacmac.citizenship.canada

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pacmac.citizenship.canada.model.QuestionObj


class AnswerRecyclerViewAdapter(private val values: List<QuestionObj>) :
        RecyclerView.Adapter<AnswerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.answer_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.questionNumber.text = "${position + 1}."
        holder.question.text = item.question
        holder.correctAnswer.text = item.getAnswerString(item.answer)
        if (!item.isCorrectAnswer()) {
            holder.incorrectAnswer.text = item.getAnswerString(item.userAnswer)
            holder.incorrectAnswer.visibility = View.VISIBLE
            holder.incorrectAnswerImage.visibility = View.VISIBLE
        } else {
            holder.incorrectAnswer.visibility = View.GONE
            holder.incorrectAnswerImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionNumber: TextView = view.findViewById(R.id.questionNumber)
        val question: TextView = view.findViewById(R.id.question)
        val correctAnswer: TextView = view.findViewById(R.id.correctAnswer)
        val incorrectAnswer: TextView = view.findViewById(R.id.incorrectAnswer)
        val incorrectAnswerImage: ImageView = view.findViewById(R.id.incorrectAnswerImage)

        override fun toString(): String {
            return ""
        }
    }


}