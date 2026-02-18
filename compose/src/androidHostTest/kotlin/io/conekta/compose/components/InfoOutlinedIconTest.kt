package io.conekta.compose.components

import androidx.compose.ui.graphics.PathFillType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class InfoOutlinedIconTest {
    @Test
    fun iconHasCorrectName() {
        assertEquals("InfoOutlined", InfoOutlinedIcon.name)
    }

    @Test
    fun iconHasCorrectDefaultWidth() {
        assertEquals(24f, InfoOutlinedIcon.defaultWidth.value)
    }

    @Test
    fun iconHasCorrectDefaultHeight() {
        assertEquals(24f, InfoOutlinedIcon.defaultHeight.value)
    }

    @Test
    fun iconHasCorrectViewportWidth() {
        assertEquals(24f, InfoOutlinedIcon.viewportWidth)
    }

    @Test
    fun iconHasCorrectViewportHeight() {
        assertEquals(24f, InfoOutlinedIcon.viewportHeight)
    }

    @Test
    fun iconIsNotNull() {
        assertNotNull(InfoOutlinedIcon)
    }

    @Test
    fun iconHasPathNodes() {
        assertTrue(InfoOutlinedIcon.root.size > 0)
    }

    @Test
    fun iconRootContainsEvenOddPath() {
        val paths =
            InfoOutlinedIcon.root
                .iterator()
                .asSequence()
                .toList()
        assertTrue(paths.isNotEmpty())
        val path = paths.first() as androidx.compose.ui.graphics.vector.VectorPath
        assertEquals(PathFillType.EvenOdd, path.pathFillType)
    }
}
