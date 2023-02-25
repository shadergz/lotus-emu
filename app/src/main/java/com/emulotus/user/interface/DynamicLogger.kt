package com.emulotus.user.`interface`

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emulotus.R
import com.emulotus.RuntimeProperties
import com.emulotus.user.DyLoggerFilter
import com.emulotus.user.LoggerMessage

class DynamicLogger(
    private val loggerMessage: List<LoggerMessage>
) : RecyclerView.Adapter<DynamicLogger.DyLoggerViewHolder>() {
    inner class DyLoggerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DyLoggerViewHolder {
        val ourHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.user_log_item,
            parent, false
        )
        return DyLoggerViewHolder(ourHolder)
    }

    private var displayFilter: DyLoggerFilter = DyLoggerFilter.WOT_FILTERS
    private var currentCount = 0

    fun updateFilter(selFilter: DyLoggerFilter) {
        // Already filtered, we don't need to redo this again
        if (displayFilter == selFilter && currentCount == loggerMessage.size) return
        else if (displayFilter != selFilter) displayFilter = selFilter

        loggerMessage.forEachIndexed { pos, msg ->
            if (msg.toFilter() == selFilter)
                notifyItemRemoved(pos)
            else
                notifyItemRemoved(pos)
        }
    }

    override fun onBindViewHolder(holder: DyLoggerViewHolder, position: Int) {
        val holderData = loggerMessage[position]
        val holderLevel = holderData.contextLevel
        val holderViewer = holder.itemView as RelativeLayout

        holderViewer.apply {
            setOnClickListener {
            }

            val logLocal = findViewById<TextView>(R.id.user_log_location)
            val logTime = findViewById<TextView>(R.id.user_log_time)
            val logCtx = findViewById<TextView>(R.id.user_log_tag)
            val logLevel = findViewById<TextView>(R.id.user_log_level)
            val logMessage = findViewById<TextView>(R.id.user_log_message)

            logLocal?.let { it.text = holderData.location }
            logTime?.let { it.text = holderData.time }
            logCtx?.apply { text = holderData.contextMessage }

            logLevel?.apply {
                text = holderLevel.letterLevel.toString()
                setBackgroundColor(
                    resources.getColor(
                        holderLevel.colorLevel,
                        RuntimeProperties.appTheme()
                    )
                )
            }
            logMessage?.let { it.text = holderData.message }
        }
    }

    override fun getItemCount(): Int {
        currentCount = loggerMessage.size
        return currentCount
    }
}