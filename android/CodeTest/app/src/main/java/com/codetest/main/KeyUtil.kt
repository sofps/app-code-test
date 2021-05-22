package com.codetest.main

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyUtil @Inject constructor(
    private val context: Context
) {

    companion object {
        const val KEY = "api_key"
    }

    private fun preferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getKey(): String {
        preferences().getString(KEY, null)?.let {
            return it
        } ?: kotlin.run {
            val apiKey = UUID.randomUUID().toString()
            preferences()
                .edit()
                .putString(KEY, apiKey)
                .apply()
            return apiKey
        }
    }
}