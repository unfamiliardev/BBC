/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.unfamiliardev.bbc.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

object UpdateChecker {

    private const val API = "https://api.github.com/repos/unfamiliardev/BBC/releases/latest"
    private const val PREF_SKIPPED = "bbc_update_skip"
    private const val KEY_SKIPPED_CODE = "skipped_version_code"

    data class UpdateInfo(
        val versionName: String,
        val versionCode: Int,
        val apkUrl: String,
        val releaseNotes: String
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun check(context: Context): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(API)
                .header("Accept", "application/vnd.github.v3+json")
                .build()
            val body = client.newCall(request).execute().use { res ->
                if (!res.isSuccessful) return@withContext null
                res.body?.string() ?: return@withContext null
            }

            val json = JSONObject(body)
            val tagName = json.getString("tag_name")   // e.g. "v1.0.42"
            val notes = json.optString("body", "").take(200)

            // Tag format: v1.0.<buildNumber>
            val remoteCode = tagName.substringAfterLast(".").toIntOrNull()
                ?: return@withContext null

            if (remoteCode <= BuildConfig.VERSION_CODE) return@withContext null

            val skipped = context.getSharedPreferences(PREF_SKIPPED, Context.MODE_PRIVATE)
                .getInt(KEY_SKIPPED_CODE, -1)
            if (skipped == remoteCode) return@withContext null

            // Find the .apk asset
            val assets = json.getJSONArray("assets")
            var apkUrl: String? = null
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                if (asset.getString("name").endsWith(".apk")) {
                    apkUrl = asset.getString("browser_download_url")
                    break
                }
            }
            apkUrl ?: return@withContext null

            UpdateInfo(tagName, remoteCode, apkUrl, notes)
        } catch (_: Exception) {
            null
        }
    }

    fun skipVersion(context: Context, versionCode: Int) {
        context.getSharedPreferences(PREF_SKIPPED, Context.MODE_PRIVATE)
            .edit().putInt(KEY_SKIPPED_CODE, versionCode).apply()
    }

    suspend fun download(
        context: Context,
        url: String,
        onProgress: (Int) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            if (!response.isSuccessful) return@withContext null
            val body = response.body ?: return@withContext null
            val total = body.contentLength()

            val file = File(context.cacheDir, "bbc-update.apk")
            file.outputStream().use { out ->
                var read = 0L
                val buf = ByteArray(8192)
                body.byteStream().use { inp ->
                    var n: Int
                    while (inp.read(buf).also { n = it } != -1) {
                        out.write(buf, 0, n)
                        read += n
                        if (total > 0) onProgress((read * 100 / total).toInt())
                    }
                }
            }
            file
        } catch (_: Exception) {
            null
        }
    }

    fun install(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}
