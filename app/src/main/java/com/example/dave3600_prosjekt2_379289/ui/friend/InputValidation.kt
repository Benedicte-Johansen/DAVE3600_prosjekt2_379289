package com.example.dave3600_prosjekt2_379289.ui.friend

object InputValidation {

    fun validateInput(name: String, phone: String, birthDate: String): String? {
        return when {
            name.isBlank() -> "Navn kan ikke være tomt"
            name.length < 2 -> "Navn må være minst 2 tegn"
            phone.isBlank() -> "Telefonnummer kan ikke være tomt"
            phone.length < 8 -> "Telefonnummer må være minst 8 siffer"
            !phone.all { it.isDigit() } -> "Telefonnummer skal kun inneholde tall"
            birthDate.isBlank() -> "Fødselsdato kan ikke være tom"
            !isValidDateFormat(birthDate) -> "Ugyldig datoformat. Bruk DD.MM.YYYY (f.eks. 16.09.1999)"
            !isValidDate(birthDate) -> "Ugyldig dato. Sjekk dag, måned og år"
            else -> null
        }
    }

    private fun isValidDateFormat(date: String): Boolean {
        return date.matches(Regex("\\d{2}.\\d{2}.\\d{4}"))
    }

    private fun isValidDate(date: String): Boolean {
        if (!isValidDateFormat(date)) return false

        val parts = date.split(".")
        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false

        return when {
            month !in 1..12 -> false
            day !in 1..31 -> false
            year < 1900 || year > 2100 -> false
            month in listOf(4, 6, 9, 11) && day > 30 -> false
            month == 2 && day > 29 -> false
            month == 2 && day == 29 && !isLeapYear(year) -> false
            else -> true
        }
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}