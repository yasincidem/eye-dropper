package com.yasincidem.eyedropper.service

import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import com.yasincidem.eyedropper.InteractionSession

class InteractionSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession = InteractionSession(this)
}