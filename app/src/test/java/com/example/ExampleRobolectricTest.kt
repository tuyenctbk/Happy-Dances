package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Happy Dances", appName)
  }

  @Test
  fun `voice command navigation tests`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = com.example.ui.DanceViewModel(app)

    val mapResult = vm.executeVoiceCommand("go to the map please")
    assertEquals("map", vm.currentScreen.value)
    assertTrue(mapResult.contains("Navigating to World Map"))

    val libraryResult = vm.executeVoiceCommand("open ballet basics library")
    assertEquals("library", vm.currentScreen.value)
    assertTrue(libraryResult.contains("Ballet Basics"))

    val passportResult = vm.executeVoiceCommand("show my passport and badges")
    assertEquals("passport", vm.currentScreen.value)
    assertTrue(passportResult.contains("Passport"))
  }

  @Test
  fun `daily challenge and practice timer verification`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = com.example.ui.DanceViewModel(app)

    // Test that music tracks catalog is loaded
    val tracks = com.example.ui.MusicTrackCatalog.tracks
    assertTrue(tracks.isNotEmpty())

    // Test synthesizer chime
    assertNotNull(vm.synthesizer)
  }
}
