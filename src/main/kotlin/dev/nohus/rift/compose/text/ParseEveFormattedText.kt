package dev.nohus.rift.compose.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import dev.nohus.rift.di.koin

@Composable
fun parseEveFormattedText(text: String, decodeEntities: Boolean = false): AnnotatedString {
    val parseEveFormattedText: ParseEveFormattedTextUseCase = remember { koin.get() }
    return parseEveFormattedText(text, decodeEntities = decodeEntities).toLinkedAnnotatedString().text
}
