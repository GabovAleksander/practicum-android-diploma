package ru.practicum.android.diploma.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.global.util.Constants
import ru.practicum.android.diploma.global.util.Mapper
import ru.practicum.android.diploma.search.domain.model.Vacancy

class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivCompanyCover: ImageView = itemView.findViewById(R.id.ivCompany)
    private val vacancyAndCity: TextView = itemView.findViewById(R.id.tvVacancyName)
    private val companyName: TextView = itemView.findViewById(R.id.tv_employer_name)
    private val salary: TextView = itemView.findViewById(R.id.tv_salary)

    fun bind(vacancy: Vacancy) {
        vacancyAndCity.text = "${vacancy.name}, ${vacancy.area}"
        companyName.text = vacancy.employer
        salary.text = vacancy.salary

        Glide.with(itemView)
            .load(vacancy.logo)
            .fitCenter().dontAnimate()
            .placeholder(R.drawable.ic_placeholder_32px)
            .transform(
                RoundedCorners(
                    Mapper.mapRadiusForGlide(
                        radius = Constants.CORNER_RADIUS_DP,
                        context = itemView.context
                    )
                )
            )
            .into(ivCompanyCover)
    }
}
