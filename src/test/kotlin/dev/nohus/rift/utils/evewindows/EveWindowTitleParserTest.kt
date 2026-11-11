package dev.nohus.rift.utils.evewindows

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class EveWindowTitleParserTest : FreeSpec({

    mapOf(
        "EVE - Nohus Bluxome" to "Nohus Bluxome",
        "EVE - 1-2'3" to "1-2'3",
        "EVE - abc" to "abc",
        "EVE - ab" to null,
        "EVE - ${"a".repeat(38)}" to null,
        "Prefix EVE - Nohus Bluxome" to null,
        "EVE - Nohus Bluxome - Suffix" to "Nohus Bluxome - Suffix",
        "eve - Nohus Bluxome" to null,
        "" to null,
    ).forEach { [title, expected] ->
        "Title '$title' parses to '$expected'" {
            EveWindowTitleParser().getCharacterName(title) shouldBe expected
        }
    }
})
