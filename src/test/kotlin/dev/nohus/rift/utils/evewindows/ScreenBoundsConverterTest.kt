package dev.nohus.rift.utils.evewindows

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.awt.GraphicsConfiguration
import java.awt.Rectangle
import java.awt.geom.AffineTransform

class ScreenBoundsConverterTest : FreeSpec({

    "converts edges relative to the selected display origin" {
        val primary = getConfiguration(Rectangle(0, 0, 1920, 1080), 1.0)
        val scaled = getConfiguration(Rectangle(1920, 0, 1280, 960), 1.5)
        val converter = ScreenBoundsConverter { listOf(primary, scaled) }

        converter.convert(
            ScreenRectangle(x = 2251, y = 151, width = 150, height = 151),
            NativeCoordinateUnit.PhysicalPixels,
        ) shouldBe ScreenBoundsConverter.Conversion(
            bounds = ScreenRectangle(x = 2141, y = 101, width = 100, height = 100),
            scale = DisplayScale(1.5, 1.5),
        )
    }

    "preserves negative display origins" {
        val scaled = getConfiguration(Rectangle(-1600, -1125, 1280, 900), 1.25)
        val converter = ScreenBoundsConverter { listOf(scaled) }

        converter.convert(
            ScreenRectangle(x = -1475, y = -1000, width = 125, height = 125),
            NativeCoordinateUnit.PhysicalPixels,
        ) shouldBe ScreenBoundsConverter.Conversion(
            bounds = ScreenRectangle(x = -1500, y = -1025, width = 100, height = 100),
            scale = DisplayScale(1.25, 1.25),
        )
    }

    "uses the nearest display for a fully off-screen window" {
        val primary = getConfiguration(Rectangle(0, 0, 1920, 1080), 1.0)
        val scaled = getConfiguration(Rectangle(1920, 0, 1280, 960), 2.0)
        val converter = ScreenBoundsConverter { listOf(primary, scaled) }

        converter.convert(
            ScreenRectangle(x = 4500, y = 100, width = 200, height = 200),
            NativeCoordinateUnit.PhysicalPixels,
        ).scale shouldBe DisplayScale(2.0, 2.0)
    }
})

private fun getConfiguration(bounds: Rectangle, scale: Double): GraphicsConfiguration {
    return mockk {
        every { this@mockk.bounds } returns bounds
        every { defaultTransform } returns AffineTransform.getScaleInstance(scale, scale)
    }
}
