package com.rio.commerce.core.base.view

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationResultTest {

    @Test
    fun testPasswordValidation() {
        assertTrue { "P@ssw0rd".validPassword }
        assertTrue { "P@ssword1".validPassword }
        assertFalse { "password".validPassword }
        assertFalse { "Pa1s".validPassword }
    }

    @Test
    fun testPhoneValidation() {
        assertTrue { "818850161".validPhone }
        assertTrue { "+62818850161".validPhone }
        assertFalse { "818".validPhone }
    }
}