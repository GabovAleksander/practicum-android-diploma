package ru.practicum.android.diploma.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.search.domain.model.Vacancy

class VacancyAdapter(
    var vacancyList: List<Vacancy>,
    private var itemClickListener: VacancyClickListener,
) : RecyclerView.Adapter<VacancyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): VacancyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vacancy_item, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val vacancy = vacancyList[position]
        holder.bind(vacancy)

        holder.itemView.setOnClickListener {
            itemClickListener.onVacancyClick(vacancy)
        }
    }

    override fun getItemCount(): Int {
        return vacancyList.size
    }

    fun interface VacancyClickListener {
        fun onVacancyClick(vacancy: Vacancy)
    }
}
