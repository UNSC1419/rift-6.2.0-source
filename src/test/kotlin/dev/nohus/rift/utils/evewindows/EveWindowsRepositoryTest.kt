package dev.nohus.rift.utils.evewindows

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

class EveWindowsRepositoryTest : FreeSpec({

    "distinguishes an empty successful snapshot from a failure" {
        val results = ArrayDeque<GetEveWindowsResult>().apply {
            add(GetEveWindowsResult.Success(emptyList()))
            add(GetEveWindowsResult.Failure("temporary failure"))
        }
        val repository = EveWindowsRepository { results.removeFirst() }

        repository.refresh()
        repository.state.value.status shouldBe EveWindowsRepository.Status.Available
        repository.state.value.windows shouldBe emptyList()

        repository.refresh()
        repository.state.value.status shouldBe EveWindowsRepository.Status.Failed("temporary failure")
        repository.state.value.windows shouldBe emptyList()
    }

    "retains the last successful snapshot after a transient failure" {
        val window = getWindow()
        val results = ArrayDeque<GetEveWindowsResult>().apply {
            add(GetEveWindowsResult.Success(listOf(window)))
            add(GetEveWindowsResult.Failure("temporary failure"))
        }
        val repository = EveWindowsRepository { results.removeFirst() }

        repository.refresh()
        val successfulUpdateTime = repository.state.value.updatedAt
        repository.refresh()

        repository.state.value.windows shouldBe listOf(window)
        repository.state.value.updatedAt shouldBe successfulUpdateTime
    }
})

private fun getWindow(): EveWindow {
    val bounds = ScreenRectangle(10, 20, 800, 600)
    val measurement = WindowBoundsMeasurement(bounds, bounds, BoundsAccuracy.Exact)
    return EveWindow(
        id = EveWindowId("test:1"),
        characterName = "Test Character",
        title = "EVE - Test Character",
        geometry = EveWindowGeometry(measurement, measurement, DisplayScale.Identity),
        state = EveWindowState.Normal,
        isActive = true,
    )
}
