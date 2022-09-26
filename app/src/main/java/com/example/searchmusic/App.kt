package com.example.searchmusic

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp : App(){}

open class App : Application() {}