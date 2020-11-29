package com.pinder.app

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import com.pinder.login.util.logout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NavigationTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()
    @Test
    @RepeatTest(10)
    fun test_login_goSettings_logout() {
      for (x in 0 until 10){
//          login()
//          logout()
      }
    }
}