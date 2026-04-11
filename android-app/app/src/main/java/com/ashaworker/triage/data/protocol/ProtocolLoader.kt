package com.ashaworker.triage.data.protocol

import android.content.Context
import com.ashaworker.triage.data.model.DangerSignsEnvelope
import com.ashaworker.triage.data.model.Protocol
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson = Gson()
) {

    fun loadProtocol(assetName: String): Protocol {
        val json = context.assets.open("protocols/$assetName").bufferedReader().use { it.readText() }
        return gson.fromJson(json, Protocol::class.java)
    }

    fun loadDangerSigns(): DangerSignsEnvelope {
        val json = context.assets.open("protocols/danger_signs.json").bufferedReader().use { it.readText() }
        return gson.fromJson(json, DangerSignsEnvelope::class.java)
    }
}
