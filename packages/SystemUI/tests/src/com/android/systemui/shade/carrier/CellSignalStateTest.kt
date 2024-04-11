/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.shade.carrier

import android.testing.AndroidTestingRunner
import androidx.test.filters.SmallTest
import com.android.systemui.SysuiTestCase
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidTestingRunner::class)
@SmallTest
class CellSignalStateTest : SysuiTestCase() {

    @Test
    fun testChangeVisibility_sameObject() {
        val c = CellSignalState()

        val other = c.changeVisibility(c.visible)

        assertSame(c, other)
    }

    @Test
    fun testChangeVisibility_otherObject() {
        val c = CellSignalState()

        val other = c.changeVisibility(!c.visible)

        assertNotSame(c, other)
    }
}
