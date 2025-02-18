package com.geby.listifyapplication.taskcard

object TaskCardDataSource {
    fun getCardData(): List<TaskCardData> {
        return listOf(
            TaskCardData(
                "Continue Android Project", "12.00 PM"
            ), TaskCardData(
                "Meet up with friends", "08.30 AM"
            ), TaskCardData(
                "Dinner with family", "07.00 PM"
            ),TaskCardData(
                "Dinner with family", "07.00 PM"
            ),TaskCardData(
                "Dinner with family", "07.00 PM"
            ),TaskCardData(
                "Dinner with family", "07.00 PM"
            ),
        )
    }
}