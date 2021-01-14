package com.harnet.sharesomephoto.service

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.harnet.sharesomephoto.R
import com.harnet.sharesomephoto.model.Sound

class SoundService(val context: Context) {
    private var soundPool: SoundPool? = null
    private lateinit var soundsInPool: IntArray

    init {
        soundPoolInit()
    }

    private fun soundPoolInit() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        fillSoundPoolWithSounds()
    }

    private fun fillSoundPoolWithSounds() {
        val sounds: List<Sound> = listOf(
            Sound("incoming_msg", R.raw.msg_sound),
            Sound("new_msg", R.raw.new_msg))

        soundsInPool = IntArray(sounds.size)
        for (i in sounds.indices) {
            soundPool?.let {
                soundsInPool[i] = it.load(context, sounds[i].source, 1)
            }
        }
    }

    fun playSound(soundName: String?) {
        when (soundName) {
            "incoming_msg" -> soundPool?.play(soundsInPool[0], 0.3f, 0.3f, 0, 0, 1f)
            "new_msg" -> soundPool?.play(soundsInPool[1], 0.3f, 0.3f, 0, 0, 1f)

        }
    }

    fun releaseSoundPool() {
        soundPool?.release()
        soundPool = null
    }
}