/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.util

import android.view.KeyEvent

class KonamiCodeDetector(private val onDetected: () -> Unit) {

    private val sequence = listOf(
        KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_DPAD_UP,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_DOWN,
        KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_DPAD_LEFT,
        KeyEvent.KEYCODE_DPAD_RIGHT,
        KeyEvent.KEYCODE_B,
        KeyEvent.KEYCODE_A
    )

    private var index = 0

    fun onKeyDown(keyCode: Int): Boolean {
        if (keyCode == sequence[index]) {
            index++
            if (index == sequence.size) {
                index = 0
                onDetected()
                return true
            }
        } else {
            index = if (keyCode == sequence[0]) 1 else 0
        }
        return false
    }

    fun reset() {
        index = 0
    }
}
