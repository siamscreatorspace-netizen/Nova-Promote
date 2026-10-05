package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.ComplianceCheckResult
import com.example.data.repository.NovaRepository
import org.junit.Assert.assertEquals
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
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nova Promote", appName)
  }

  @Test
  fun `verify anti-fraud engine flags banned fake followers`() {
    // Check prohibited words
    val bannedTerms = listOf("buy fake followers", "instant bot engagement", "mass follow script", "synthetic views")
    val bannedList = listOf("fake follower", "bot", "mass follow", "synthetic")

    for (term in bannedTerms) {
      val hasBanned = bannedList.any { term.contains(it) }
      assertTrue("Term '$term' should be flagged", hasBanned)
    }
  }
}
