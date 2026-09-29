enum class CaseFormat {
    PRESERVE,
    LOWERCASE,
    UPPERCASE,
    SNAKE_CASE,
    KEBAB_CASE;

    fun apply(input: String): String {
        return when (this) {
            PRESERVE -> input
            LOWERCASE -> input.lowercase()
            UPPERCASE -> input.uppercase()
            SNAKE_CASE -> toDelimited(input, '_')
            KEBAB_CASE -> toDelimited(input, '-')
        }
    }

    private fun toDelimited(input: String, delimiter: Char): String {
        if (input.isEmpty()) return input
        val result = StringBuilder()
        for (i in input.indices) {
            val c = input[i]
            if (c.isUpperCase()) {
                if (i > 0 && input[i - 1] != delimiter && (input[i - 1].isLowerCase() || (i + 1 < input.length && input[i + 1].isLowerCase()))) {
                    result.append(delimiter)
                }
                result.append(c.lowercaseChar())
            } else if (c == '_' || c == '-') {
                result.append(delimiter)
            } else {
                result.append(c)
            }
        }
        return result.toString()
    }
}
