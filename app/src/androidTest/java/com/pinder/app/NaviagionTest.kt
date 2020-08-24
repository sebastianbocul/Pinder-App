package com.pinder.app

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.pinder.app.util.RepeatRule
import com.pinder.app.util.RepeatTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NaviagionTest {
    @Rule
    @JvmField
    var repeatRule: RepeatRule = RepeatRule()
    @Test
    @RepeatTest(2)
    fun test_login_goSettings_logout() {
//        login()
//        logout()
    }
}