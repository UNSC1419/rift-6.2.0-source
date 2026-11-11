package dev.nohus.rift.intel.state

import dev.nohus.rift.intel.state.IntelStateController.Dated
import dev.nohus.rift.standings.isFriendly

/** Estimates entities without counting character and ship reports of the same group twice. */
fun getTotalIntelEntityCount(intel: List<Dated<SystemEntity>>?): Int = getIntelEntityCount(intel, includeFriendly = true)

fun getHostileIntelEntityCount(intel: List<Dated<SystemEntity>>?): Int = getIntelEntityCount(intel, includeFriendly = false)

private fun getIntelEntityCount(intel: List<Dated<SystemEntity>>?, includeFriendly: Boolean): Int {
    val active = intel.orEmpty().filterNot { it.isCleared }.map { it.item }
    val characters = active.filterIsInstance<SystemEntity.Character>()
        .count { includeFriendly || !it.details.standingLevel.isFriendly } +
        active.filterIsInstance<SystemEntity.UnspecifiedCharacter>().sumOf { it.count }
    val ships = active.filterIsInstance<SystemEntity.Ship>()
        .filter { includeFriendly || it.standing?.isFriendly != true }
        .sumOf { it.count }
    var count = maxOf(characters, ships)
    if (active.any { it is SystemEntity.GateCamp }) count += 3
    if (active.any { it is SystemEntity.Spike }) count += 10
    return count
}
