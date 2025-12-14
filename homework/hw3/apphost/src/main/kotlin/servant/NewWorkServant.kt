package ru.hse.apphost.servant

import ru.hse.analyse.api.AnalyseService
import ru.hse.core.Meta
import ru.hse.core.makeIf
import ru.hse.store.api.StoreService

class NewWorkServant(private val storeService: StoreService, private val analyseService: AnalyseService) {
    suspend fun uploadNewWork(meta: Meta, work: ByteArray): Boolean {
        println("Uploading new work $meta")
        val analysed = analyseService.analyse(meta, work)
        println("Analysed: $analysed")
        return (makeIf(analysed) { storeService.save(meta, work) } ?: return false).also {
            println("Saved: $it")
        }
    }
}