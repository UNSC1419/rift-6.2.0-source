package dev.nohus.rift.overlay.window;

import androidx.compose.ui.input.pointer.AwtCursor;
import androidx.compose.ui.input.pointer.PointerIcon;
import java.awt.Cursor;

final class OverlayPointerIcons {
    private OverlayPointerIcons() {}

    // Compose Desktop wraps PointerIcon(Cursor) in internal AwtCursor. Kotlin code in this project
    // cannot access that type, but Java can access its JVM class and return the Cursor to Kotlin.
    // Keeping the dependency here so a Compose upgrade fails compilation if it changes.
    static Cursor getCursor(PointerIcon icon) {
        return icon instanceof AwtCursor awtCursor ? awtCursor.getCursor() : Cursor.getDefaultCursor();
    }
}
