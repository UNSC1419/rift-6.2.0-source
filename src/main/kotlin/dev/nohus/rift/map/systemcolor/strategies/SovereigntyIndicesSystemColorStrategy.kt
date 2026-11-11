package dev.nohus.rift.map.systemcolor.strategies

import dev.nohus.rift.map.systemcolor.PercentageSystemColorStrategy
import dev.nohus.rift.repositories.MapStatusRepository.SolarSystemStatus

class SovereigntyIndicesSystemColorStrategy(
    private val systemStatus: Map<Int, SolarSystemStatus>,
    private val index: SovereigntyIndex,
) : PercentageSystemColorStrategy() {

    enum class SovereigntyIndex {
        Strategic,
        Military,
        Industrial,
        ActivityDefenseMultiplier,
    }

    override fun hasData(system: Int): Boolean {
        return systemStatus[system]?.sovereignty?.alliance != null
    }

    override fun getPercentage(system: Int): Float {
        val development = systemStatus[system]?.sovereignty?.alliance?.development ?: return 0f
        val (value, maximum) = when (index) {
            SovereigntyIndex.Strategic -> development.strategicLevel.toDouble() to 5.0
            SovereigntyIndex.Military -> development.militaryLevel.toDouble() to 5.0
            SovereigntyIndex.Industrial -> development.industrialLevel.toDouble() to 5.0
            SovereigntyIndex.ActivityDefenseMultiplier -> development.activityDefenseMultiplier to 6.0
        }
        return (value / maximum).toFloat().coerceIn(0f, 1f)
    }
}
