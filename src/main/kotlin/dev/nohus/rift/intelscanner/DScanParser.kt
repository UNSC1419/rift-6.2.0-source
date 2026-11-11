package dev.nohus.rift.intelscanner

import dev.nohus.rift.utils.get
import org.koin.core.annotation.Single

@Single
class DScanParser {

    private val dScanLineRegex = """^(?<typeId>\d+)\t(?<name>.*)\t(?<typeName>.*)\t(?<distance>[0-9,]+ (m|km)|-)$""".toRegex()
    companion object {
        private const val ASTRONOMICAL_UNIT = 149_597_870_700
    }

    data class DScanEntity(
        val typeId: Int,
        val typeName: String,
        val name: String,
        val distance: Long?,
    )

    fun isDScanLine(line: String): Boolean {
        return line.matches(dScanLineRegex)
    }

    fun parseDScan(lines: List<String>): List<DScanEntity>? {
        return lines.mapNotNull(::parseDscanLine).takeIf { it.isNotEmpty() }
    }

    private fun parseDscanLine(line: String): DScanEntity? {
        val match = dScanLineRegex.find(line) ?: return null
        return DScanEntity(
            typeId = match["typeId"].toIntOrNull() ?: return null,
            typeName = match["typeName"],
            name = match["name"],
            distance = parseDistance(match["distance"]),
        )
    }

    private fun parseDistance(distance: String): Long? {
        if (distance == "-") return null
        val [value, unit] = distance.split(" ")
        val cleanValue = value.replace(",", "")
        return when (unit.lowercase()) {
            "m" -> cleanValue.toLongOrNull()
            "km" -> cleanValue.toLongOrNull()?.let { it * 1000 }
            "au" -> cleanValue.toLongOrNull()?.let { it * ASTRONOMICAL_UNIT }
            else -> null
        }
    }
}
