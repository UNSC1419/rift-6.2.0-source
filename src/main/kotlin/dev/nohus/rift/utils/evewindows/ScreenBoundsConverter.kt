package dev.nohus.rift.utils.evewindows

import org.koin.core.annotation.Single
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Single
class ScreenBoundsConverter(
    private val getConfigurations: () -> List<GraphicsConfiguration> = {
        GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
            .flatMap { it.configurations.asIterable() }
    },
) {

    data class Conversion(
        val bounds: ScreenRectangle,
        val scale: DisplayScale,
    )

    fun convert(bounds: ScreenRectangle, unit: NativeCoordinateUnit): Conversion {
        if (unit == NativeCoordinateUnit.LogicalPoints) {
            return Conversion(bounds, getScaleAtLogicalBounds(bounds))
        }
        return try {
            val configuration = getConfigurationForPhysicalBounds(bounds)
            if (configuration == null) {
                Conversion(bounds, DisplayScale.Identity)
            } else {
                val transform = configuration.defaultTransform
                val displayBounds = configuration.bounds
                val left = convertEdge(bounds.x, displayBounds.x, transform.scaleX)
                val top = convertEdge(bounds.y, displayBounds.y, transform.scaleY)
                val right = convertEdge(bounds.right, displayBounds.x, transform.scaleX)
                val bottom = convertEdge(bounds.bottom, displayBounds.y, transform.scaleY)
                Conversion(
                    bounds = ScreenRectangle(left, top, right - left, bottom - top),
                    scale = DisplayScale(transform.scaleX, transform.scaleY),
                )
            }
        } catch (_: Exception) {
            Conversion(bounds, DisplayScale.Identity)
        }
    }

    private fun getConfigurationForPhysicalBounds(bounds: ScreenRectangle): GraphicsConfiguration? {
        return getConfigurations()
            .maxWithOrNull(
                compareBy<GraphicsConfiguration> {
                    getIntersectionArea(bounds, it.getPhysicalBounds())
                }.thenBy {
                    -getDistanceSquared(bounds, it.getPhysicalBounds())
                },
            )
    }

    private fun getScaleAtLogicalBounds(bounds: ScreenRectangle): DisplayScale {
        return try {
            val configuration = getConfigurations()
                .maxWithOrNull(
                    compareBy<GraphicsConfiguration> {
                        getIntersectionArea(bounds, it.bounds.toScreenRectangle())
                    }.thenBy {
                        -getDistanceSquared(bounds, it.bounds.toScreenRectangle())
                    },
                )
            configuration?.defaultTransform?.let { DisplayScale(it.scaleX, it.scaleY) } ?: DisplayScale.Identity
        } catch (_: Exception) {
            DisplayScale.Identity
        }
    }

    private fun getIntersectionArea(first: ScreenRectangle, second: ScreenRectangle): Long {
        val width = max(0, min(first.right, second.right) - max(first.x, second.x))
        val height = max(0, min(first.bottom, second.bottom) - max(first.y, second.y))
        return width.toLong() * height
    }

    private fun getDistanceSquared(first: ScreenRectangle, second: ScreenRectangle): Long {
        val horizontalDistance = max(0, max(first.x, second.x) - min(first.right, second.right)).toLong()
        val verticalDistance = max(0, max(first.y, second.y) - min(first.bottom, second.bottom)).toLong()
        return horizontalDistance * horizontalDistance + verticalDistance * verticalDistance
    }

    private fun GraphicsConfiguration.getPhysicalBounds(): ScreenRectangle {
        val transform = defaultTransform
        return ScreenRectangle(
            x = bounds.x,
            y = bounds.y,
            width = (bounds.width * transform.scaleX).roundToInt(),
            height = (bounds.height * transform.scaleY).roundToInt(),
        )
    }

    private fun convertEdge(edge: Int, displayOrigin: Int, scale: Double): Int {
        return (displayOrigin + (edge - displayOrigin) / scale).roundToInt()
    }

    private fun java.awt.Rectangle.toScreenRectangle(): ScreenRectangle {
        return ScreenRectangle(x, y, width, height)
    }
}
