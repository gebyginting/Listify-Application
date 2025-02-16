package com.geby.quizup

import com.geby.listifyapplication.R
import com.geby.listifyapplication.categorycards.CategoryCardData

object CategoryCardDataSource {
    fun getCardData(): List<CategoryCardData> {
        return listOf(
            CategoryCardData(
                R.drawable.completed_icon, "Completed", 3
            ),
            CategoryCardData(
                R.drawable.not_done_icon, "Not Done", 4
            ),
            CategoryCardData(
                R.drawable.canceled_icon, "Canceled", 5
            ),
            CategoryCardData(
                R.drawable.on_going_icon, "On Going", 5
            )
        )
    }
}