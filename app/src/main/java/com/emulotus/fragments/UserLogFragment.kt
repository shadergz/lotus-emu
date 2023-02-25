package com.emulotus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emulotus.R
import com.emulotus.user.DyLoggerFilter
import com.emulotus.user.LogMessageLevel
import com.emulotus.user.LoggerMessage
import com.emulotus.user.`interface`.DynamicLogger

class UserLogFragment : Fragment() {

    private lateinit var logListDynamic: RecyclerView
    private lateinit var logAdapter: DynamicLogger

    private var logTestMessage = mutableListOf(
        LoggerMessage(
            LogMessageLevel.SuccessMessage, "Test success",
            "Hey what's up, everything is ok!"
        ),
        LoggerMessage(LogMessageLevel.InfoMessage, "Test info (Wout context)"),
        LoggerMessage(
            LogMessageLevel.WarnMessage, "Test warn", "The virtual machine was" +
                    " halt"
        ),
        LoggerMessage(LogMessageLevel.DevMessage, "Test dev", "Maybe a bug?! Report now!"),
        LoggerMessage(LogMessageLevel.ErrorMessage, "Test info", "Backend was clashed!")
    )

    override fun onStart() {
        super.onStart()
        val recycle = view?.findViewById<RecyclerView>(R.id.log_list)
        recycle?.let { logListDynamic = it }
        logAdapter = DynamicLogger(logTestMessage)

        logListDynamic.adapter = logAdapter

        logAdapter.updateFilter(DyLoggerFilter.WARNING_FILTER)
        logListDynamic.layoutManager = LinearLayoutManager(view?.context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_userlog, container, false)
    }
}