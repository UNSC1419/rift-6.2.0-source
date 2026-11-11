package dev.nohus.rift.map.systemcolor.strategies

import dev.nohus.rift.intel.state.IntelStateController
import dev.nohus.rift.intel.state.SystemEntity
import dev.nohus.rift.intel.state.getHostileIntelEntityCount
import dev.nohus.rift.map.systemcolor.PercentageSystemColorStrategy
import dev.nohus.rift.map.systemcolor.PercentageSystemColorStrategyPalettes

class HostileEntitiesSystemColorStrategy(
    private val getIntel: (system: Int) -> List<IntelStateController.Dated<SystemEntity>>,
) : PercentageSystemColorStrategy(PercentageSystemColorStrategyPalettes.negative) {

    override fun hasData(system: Int): Boolean {
        return getHostileIntelEntityCount(getIntel(system)) > 0
    }

    override fun getPercentage(system: Int): Float {
        return (getHostileIntelEntityCount(getIntel(system)) / 5f).coerceIn(0f..1f)
    }
}
