package ru.lifelaboratory.finopolis

import android.app.Application
import com.vk.sdk.VKSdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // инициализация социальных сетей
        VKSdk.initialize(this)
    }

}