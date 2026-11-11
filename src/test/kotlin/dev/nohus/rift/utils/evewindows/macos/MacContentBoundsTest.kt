package dev.nohus.rift.utils.evewindows.macos

import dev.nohus.rift.utils.evewindows.ScreenRectangle
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class MacContentBoundsTest : FreeSpec({

    "selects the game content rectangle from accessibility descendants" {
        val frame = ScreenRectangle(100, 200, 1280, 800)
        val content = ScreenRectangle(101, 228, 1278, 771)
        val largePanel = ScreenRectangle(100, 228, 850, 770)

        selectMacContentBounds(frame, sequenceOf(largePanel, content)) shouldBe content
    }

    "places content below the measured title bar" {
        val frame = ScreenRectangle(100, 200, 1280, 800)
        val closeButton = ScreenRectangle(112, 207, 14, 14)

        estimateMacContentBoundsFromTitleBar(frame, listOf(closeButton)) shouldBe
            ScreenRectangle(100, 228, 1280, 772)
    }

    "does not treat a button in the game content as a title bar button" {
        val frame = ScreenRectangle(100, 200, 1280, 800)
        val contentButton = ScreenRectangle(112, 350, 14, 14)

        estimateMacContentBoundsFromTitleBar(frame, listOf(contentButton)) shouldBe null
    }

    "leaves content unavailable without a measured title bar" {
        estimateMacContentBoundsFromTitleBar(ScreenRectangle(100, 200, 1280, 800), emptyList()) shouldBe null
    }
})
