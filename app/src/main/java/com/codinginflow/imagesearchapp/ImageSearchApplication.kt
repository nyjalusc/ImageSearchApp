package com.codinginflow.imagesearchapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Dagger Hilt requires Application class to be subclassed.
 * Do not forget to register it in the manifest.
 */
@HiltAndroidApp
class ImageSearchApplication : Application() {
}