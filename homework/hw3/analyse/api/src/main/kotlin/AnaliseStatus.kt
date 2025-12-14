package ru.hse.analyse.api

import kotlinx.serialization.Serializable

@Serializable
sealed interface AnaliseStatus {
    @Serializable
    data object Started : AnaliseStatus

    @Serializable
    data class Receiving(val percentage: Double) : AnaliseStatus

    @Serializable
    data class Running(val percentage: Double) : AnaliseStatus


    @Serializable
    data class Done(val report: Report) : AnaliseStatus
}