package com.emulotus.user

import com.emulotus.R
import java.time.LocalDateTime

const val FRONT_END_MESSAGE = "LotusFront"

enum class LogMessageId {
    SUCCESS_MSG,
    INFO_MSG,
    WARNING_MSG,
    DEV_MSG,
    ERROR_MSG,
}

sealed class LogMessageLevel(level: LogMessageId) {

    private val levelLetters = arrayOf('S', 'I', 'W', 'D', 'E')
    private val levelColors = arrayOf(
        R.color.radio_theme_success, R.color.radio_theme_info,
        R.color.radio_theme_warn, R.color.radio_theme_dev, R.color.radio_theme_err
    )

    var letterLevel: Char
    var colorLevel: Int

    init {
        letterLevel = levelLetters[level.ordinal]
        colorLevel = levelColors[level.ordinal]
    }

    object SuccessMessage : LogMessageLevel(LogMessageId.SUCCESS_MSG)
    object InfoMessage : LogMessageLevel(LogMessageId.INFO_MSG)
    object WarnMessage : LogMessageLevel(LogMessageId.WARNING_MSG)
    object DevMessage : LogMessageLevel(LogMessageId.DEV_MSG)
    object ErrorMessage : LogMessageLevel(LogMessageId.ERROR_MSG)
}

class LoggerMessage {
    constructor(wildcard: LogMessageLevel, context: String, msg: String) {
        contextLevel = wildcard
        message = msg
        contextMessage = context

        // Saving the actual time and date as a immutable string
        val current = LocalDateTime.now()
        current?.let {
            time = "$it"
        }
    }

    constructor(wildcard: LogMessageLevel, msg: String) {
        contextLevel = wildcard
        message = msg

        // Saving the actual time and date as a immutable string
        val current = LocalDateTime.now()
        current?.let {
            time = "$it"
        }
    }

    fun toFilter(): DyLoggerFilter {
        val filter = when (contextLevel) {
            LogMessageLevel.SuccessMessage -> DyLoggerFilter.SUCCESS_FILTER
            LogMessageLevel.InfoMessage -> DyLoggerFilter.INFO_FILTER
            LogMessageLevel.ErrorMessage -> DyLoggerFilter.ERROR_FILTER
            LogMessageLevel.WarnMessage -> DyLoggerFilter.WARNING_FILTER
            LogMessageLevel.DevMessage -> DyLoggerFilter.DEV_FILTER
        }
        return filter
    }

    var location = FRONT_END_MESSAGE
    var contextLevel: LogMessageLevel
    var message: String
    var contextMessage = "Not Specified"
    lateinit var time: String
}
