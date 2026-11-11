package dev.nohus.rift.overlay.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerKeyboardModifiers
import androidx.compose.ui.platform.PlatformContext
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.nohus.rift.compose.theme.Cursors
import dev.nohus.rift.utils.evewindows.EveWindowId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Surface
import org.jetbrains.skia.impl.BufferUtil
import org.jetbrains.skiko.toBufferedImage
import java.awt.AlphaComposite
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Window as AwtWindowHost
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.BitSet
import java.util.zip.CRC32
import java.util.zip.CRC32C
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.Timer

private val logger = KotlinLogging.logger {}

internal interface OverlayStacking {
    fun update(window: AwtWindowHost, isActive: Boolean)
    fun onOpened(window: AwtWindowHost)
    fun onPeerReady(window: AwtWindowHost) = Unit
    fun onClosed() = Unit
}

enum class OverlayWindowFailureReason {
    FrameTooLarge,
    NativeFailure,
}

@OptIn(InternalComposeUiApi::class)
internal class OffscreenComposeComponent(
    private val window: AwtWindowHost,
    private val eveWindowId: EveWindowId,
    private val platform: OverlayWindowPlatform,
    private var sceneWidth: Int,
    private var sceneHeight: Int,
    private val onMouseExited: () -> Unit,
    private val onNativeWindowInvalidated: (OverlayWindowFailureReason) -> Unit,
    private val content: @Composable () -> Unit,
) : JComponent() {
    private var output: OverlayFrameOutput? = null
    private var sceneDensity = platform.getInitialDensity(window)
    private val platformContext = object : PlatformContext by PlatformContext.Empty() {
        override val windowInfo = object : WindowInfo {
            override val isWindowFocused = true
            override val containerSize get() = IntSize(sceneWidth, sceneHeight)
            override val containerDpSize get() = DpSize((sceneWidth / sceneDensity).dp, (sceneHeight / sceneDensity).dp)
        }

        override fun setPointerIcon(pointerIcon: PointerIcon) {
            cursor = OverlayPointerIcons.getCursor(pointerIcon)
        }
    }
    private var scene = createScene()
    private var inputMask: OverlayInputMask? = null
    private var lastWindowsSizeCheckNanos = 0L
    private val renderTimer = Timer(FRAME_DELAY_MILLIS) {
        if (platform.shouldPollSize) {
            val now = System.nanoTime()
            if (now - lastWindowsSizeCheckNanos >= WINDOWS_SIZE_CHECK_INTERVAL_NANOS) {
                lastWindowsSizeCheckNanos = now
                updateSceneSize()
            }
        }
        inputMask?.refreshPointer()
        if (isNativeWindowValid) {
            if (!platform.isOutputValid(output)) {
                invalidateNativeWindow(OverlayWindowFailureReason.NativeFailure)
            } else if (scene.hasInvalidations()) {
                renderFrame()
            }
        }
    }
    private val windowFocusListener = object : WindowAdapter() {
        override fun windowLostFocus(event: WindowEvent) {
            inputMask?.onMouseReleased()
        }
    }
    private var isRendering = false
    private var isNativeWindowValid = true

    init {
        window.addWindowFocusListener(windowFocusListener)
        isOpaque = false
        isFocusable = true
        cursor = Cursors.pointer
        val mouseAdapter = object : MouseAdapter() {
            override fun mousePressed(event: MouseEvent) {
                inputMask?.onMousePressed()
                requestFocusInWindow()
                sendPointerEvent(event, PointerEventType.Press)
            }

            override fun mouseReleased(event: MouseEvent) {
                inputMask?.onMouseReleased()
                sendPointerEvent(event, PointerEventType.Release)
            }

            override fun mouseEntered(event: MouseEvent) {
                sendPointerEvent(event, PointerEventType.Enter)
            }

            override fun mouseExited(event: MouseEvent) {
                onMouseExited()
                sendPointerEvent(event, PointerEventType.Exit)
            }

            override fun mouseMoved(event: MouseEvent) {
                sendPointerEvent(event, PointerEventType.Move)
            }

            override fun mouseDragged(event: MouseEvent) {
                sendPointerEvent(event, PointerEventType.Move)
            }
            override fun mouseWheelMoved(event: MouseWheelEvent) {
                scene.sendPointerEvent(
                    eventType = PointerEventType.Scroll,
                    position = event.position,
                    scrollDelta = Offset(0f, event.preciseWheelRotation.toFloat()),
                    timeMillis = event.`when`,
                    buttons = event.pointerButtons,
                    keyboardModifiers = event.pointerKeyboardModifiers,
                    nativeEvent = event,
                )
            }
        }
        addMouseListener(mouseAdapter)
        addMouseMotionListener(mouseAdapter)
        addMouseWheelListener(mouseAdapter)
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(event: ComponentEvent) {
                updateSceneSize()
            }

            override fun componentMoved(event: ComponentEvent) {
                if (platform.shouldUpdateSizeOnMove) updateSceneSize()
            }
        })
    }

    fun startRendering() {
        if (isRendering || !isNativeWindowValid) return
        inputMask = platform.createInputMask(window)
        updateSceneSize()
        if (!isNativeWindowValid) return
        isRendering = true
        renderFrame()
        if (isNativeWindowValid) renderTimer.start()
    }

    override fun removeNotify() {
        renderTimer.stop()
        window.removeWindowFocusListener(windowFocusListener)
        inputMask?.close()
        scene.close()
        output?.close()
        super.removeNotify()
    }

    override fun paintComponent(graphics: Graphics) {
        (output as? RasterFrameOutput)?.paint(graphics)
    }

    fun updateSceneSize() {
        if (!isDisplayable || !isNativeWindowValid) return
        val size = platform.getSceneSize(window, this)
        if (size == null) {
            invalidateNativeWindow(OverlayWindowFailureReason.NativeFailure)
            return
        }
        val [width, height] = size
        val density = platform.getSceneDensity(window, width)
        if (sceneDensity != density) {
            sceneDensity = density
            scene.density = Density(density)
        }
        resizeScene(width, height)
    }

    private fun resizeScene(width: Int, height: Int) {
        if (width <= 0 || height <= 0 || width == sceneWidth && height == sceneHeight) return

        scene.close()
        output?.close()
        output = null
        sceneWidth = width
        sceneHeight = height
        scene = createScene()
        if (isRendering) renderFrame()
    }

    private fun createScene() = CanvasLayersComposeScene(
        density = Density(sceneDensity),
        size = IntSize(sceneWidth, sceneHeight),
        platformContext = platformContext,
    ).also { it.setContent(content) }

    private fun renderFrame() {
        if (sceneWidth.toLong() * sceneHeight > platform.maxFramePixels) {
            logger.warn { "Overlay frame exceeds the ${platform.maxFramePixels / 1_000_000} megapixel limit" }
            invalidateNativeWindow(OverlayWindowFailureReason.FrameTooLarge)
            return
        }
        val output = output ?: createOutput().also { output = it }
        output.surface.canvas.clear(Color.TRANSPARENT)
        scene.render(output.surface.canvas.asComposeCanvas(), System.nanoTime())
        if (!output.present()) invalidateNativeWindow(OverlayWindowFailureReason.NativeFailure)
    }

    private fun createOutput(): OverlayFrameOutput =
        platform.createFrameOutput(this, window, eveWindowId, sceneWidth, sceneHeight, inputMask)

    private fun invalidateNativeWindow(reason: OverlayWindowFailureReason) {
        if (!isNativeWindowValid) return
        isNativeWindowValid = false
        isRendering = false
        renderTimer.stop()
        SwingUtilities.invokeLater { onNativeWindowInvalidated(reason) }
    }

    private fun sendPointerEvent(event: MouseEvent, eventType: PointerEventType) {
        scene.sendPointerEvent(
            eventType = eventType,
            position = event.position,
            timeMillis = event.`when`,
            buttons = event.pointerButtons,
            keyboardModifiers = event.pointerKeyboardModifiers,
            nativeEvent = event,
            button = event.changedPointerButton,
        )
    }

    private val MouseEvent.position get() = platform.getPointerPosition(this, this@OffscreenComposeComponent, sceneWidth, sceneHeight)
    private val MouseEvent.pointerButtons get() = PointerButtons(
        isPrimaryPressed = (modifiersEx and MouseEvent.BUTTON1_DOWN_MASK) != 0 ||
            id == MouseEvent.MOUSE_PRESSED && button == MouseEvent.BUTTON1,
        isSecondaryPressed = (modifiersEx and MouseEvent.BUTTON3_DOWN_MASK) != 0 ||
            id == MouseEvent.MOUSE_PRESSED && button == MouseEvent.BUTTON3,
        isTertiaryPressed = (modifiersEx and MouseEvent.BUTTON2_DOWN_MASK) != 0 ||
            id == MouseEvent.MOUSE_PRESSED && button == MouseEvent.BUTTON2,
    )
    private val MouseEvent.pointerKeyboardModifiers get() = PointerKeyboardModifiers(
        isCtrlPressed = (modifiersEx and InputEvent.CTRL_DOWN_MASK) != 0,
        isMetaPressed = (modifiersEx and InputEvent.META_DOWN_MASK) != 0,
        isAltPressed = (modifiersEx and InputEvent.ALT_DOWN_MASK) != 0,
        isShiftPressed = (modifiersEx and InputEvent.SHIFT_DOWN_MASK) != 0,
        isAltGraphPressed = (modifiersEx and InputEvent.ALT_GRAPH_DOWN_MASK) != 0,
    )
    private val MouseEvent.changedPointerButton get() = when (button) {
        MouseEvent.BUTTON1 -> PointerButton.Primary
        MouseEvent.BUTTON2 -> PointerButton.Tertiary
        MouseEvent.BUTTON3 -> PointerButton.Secondary
        else -> null
    }

    private companion object {
        const val FRAME_DELAY_MILLIS = 16
        const val WINDOWS_SIZE_CHECK_INTERVAL_NANOS = 250_000_000L
    }
}

internal interface OverlayFrameOutput : AutoCloseable {
    val surface: Surface
    fun present(): Boolean
}

internal class RasterFrameOutput(
    private val component: JComponent,
    private val width: Int,
    private val height: Int,
    private val inputMask: OverlayInputMask,
) : OverlayFrameOutput {
    private val bitmap = createBitmap(width, height)
    private val frame = bitmap.toBufferedImage()
    private val pixelBytes = bitmap.createPixelBuffer()
    private val pixels = pixelBytes.asIntBuffer()
    private val segmentsPerRow = (width + SEGMENT_WIDTH - 1) / SEGMENT_WIDTH
    private val previousRowChecksums = LongArray(height)
    private val previousChecksums = LongArray(height * segmentsPerRow)
    private val opaquePixels = BitSet(width * height)
    private val checksumC = CRC32C()
    private val checksum = CRC32()
    private var hasRenderedFrame = false
    override val surface: Surface = Surface.makeRasterDirect(
        ImageInfo.makeS32(width, height, ColorAlphaType.PREMUL),
        checkNotNull(bitmap.peekPixels()).addr,
        bitmap.rowBytes,
    )

    override fun present(): Boolean {
        var left = width
        var top = height
        var right = -1
        var bottom = -1
        var inputShapeChanged = !hasRenderedFrame
        repeat(height) { y ->
            val rowByteOffset = y * bitmap.rowBytes
            pixelBytes.clear()
            pixelBytes.position(rowByteOffset)
            pixelBytes.limit(rowByteOffset + width * Int.SIZE_BYTES)
            checksumC.reset()
            checksumC.update(pixelBytes)
            val rowChecksum = checksumC.value
            if (hasRenderedFrame && previousRowChecksums[y] == rowChecksum) return@repeat
            previousRowChecksums[y] = rowChecksum
            repeat(segmentsPerRow) { segment ->
                val startX = segment * SEGMENT_WIDTH
                val endX = minOf(startX + SEGMENT_WIDTH, width)
                val byteOffset = rowByteOffset + startX * Int.SIZE_BYTES
                pixelBytes.clear()
                pixelBytes.position(byteOffset)
                pixelBytes.limit(byteOffset + (endX - startX) * Int.SIZE_BYTES)
                checksumC.reset()
                checksumC.update(pixelBytes)
                pixelBytes.position(byteOffset)
                checksum.reset()
                checksum.update(pixelBytes)
                val checksumIndex = y * segmentsPerRow + segment
                val currentChecksum = checksumC.value shl 32 or checksum.value
                if (!hasRenderedFrame || previousChecksums[checksumIndex] != currentChecksum) {
                    previousChecksums[checksumIndex] = currentChecksum
                    left = minOf(left, startX)
                    top = minOf(top, y)
                    right = maxOf(right, endX - 1)
                    bottom = maxOf(bottom, y)
                    for (x in startX until endX) {
                        val index = y * width + x
                        val isOpaque = !pixels[y * bitmap.rowBytes / Int.SIZE_BYTES + x].isInputTransparent()
                        if (opaquePixels[index] != isOpaque) {
                            opaquePixels.set(index, isOpaque)
                            inputShapeChanged = true
                        }
                    }
                }
            }
        }
        hasRenderedFrame = true
        if (inputShapeChanged) inputMask.update(createInputRectangles(), width, height)
        if (right >= left && bottom >= top) {
            drawFrame(left, top, right - left + 1, bottom - top + 1)
        }
        return true
    }

    fun paint(graphics: Graphics) {
        val frameGraphics = graphics.create() as Graphics2D
        try {
            frameGraphics.composite = AlphaComposite.Src
            frameGraphics.drawImage(frame, 0, 0, null)
        } finally {
            frameGraphics.dispose()
        }
    }

    private fun drawFrame(x: Int, y: Int, width: Int, height: Int) {
        val frameGraphics = component.graphics?.create() as? Graphics2D ?: return
        try {
            frameGraphics.composite = AlphaComposite.Src
            frameGraphics.drawImage(frame, x, y, x + width, y + height, x, y, x + width, y + height, null)
        } finally {
            frameGraphics.dispose()
        }
    }

    private fun createInputRectangles(): List<InputRectangle> {
        val rectangles = mutableListOf<InputRectangle>()
        repeat(height) { y ->
            val rowStart = y * width
            val rowEnd = rowStart + width
            var start = opaquePixels.nextSetBit(rowStart)
            while (start in rowStart until rowEnd) {
                val end = minOf(opaquePixels.nextClearBit(start), rowEnd)
                rectangles += InputRectangle(x = start - rowStart, y = y, width = end - start, height = 1)
                start = opaquePixels.nextSetBit(end)
            }
        }
        return rectangles
    }

    private fun Int.isInputTransparent() = this and ALPHA_MASK == 0

    override fun close() {
        surface.close()
        bitmap.close()
    }

    private companion object {
        const val ALPHA_MASK = 0xFF000000.toInt()
        const val SEGMENT_WIDTH = 256

        fun createBitmap(width: Int, height: Int) = Bitmap().apply {
            check(allocPixelsFlags(ImageInfo.makeS32(width, height, ColorAlphaType.PREMUL), false)) {
                "Could not allocate overlay raster"
            }
        }

        fun Bitmap.createPixelBuffer(): ByteBuffer {
            val pixelMemory = peekPixels() ?: error("Could not access overlay bitmap pixels")
            return BufferUtil.getByteBufferFromPointer(pixelMemory.addr, pixelMemory.computeByteSize())
                .order(ByteOrder.nativeOrder())
        }
    }
}

internal data class InputRectangle(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
)

internal interface OverlayInputMask : AutoCloseable {
    fun update(rectangles: List<InputRectangle>, width: Int, height: Int)
    fun refreshPointer() = Unit
    fun onMousePressed() = Unit
    fun onMouseReleased() = Unit
}

internal fun EveWindowId.getNativeId(prefix: String): Long? {
    if (!value.startsWith(prefix)) return null
    return value.removePrefix(prefix).toULongOrNull(16)?.toLong()
}
