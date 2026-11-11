package dev.nohus.rift.utils.evewindows

fun interface GetEveWindowsUseCase {
    operator fun invoke(): GetEveWindowsResult
}
