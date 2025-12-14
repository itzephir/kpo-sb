package ru.hse.analyse.analyser

class Analyser {
    /**
     * Returns percentage of plagiarism
     */
    fun analyse(work: ByteArray, works: List<ByteArray>): Double = works.fold(0.0) { acc, prevWork ->
        val wholeSize = prevWork.size
        if (work.contentEquals(prevWork)) return@fold acc
        val total = (work zip prevWork).fold(initial = 0) { acc, (workByte, prevWorkByte) ->
            println("analysing $workByte")
            acc + if (workByte == prevWorkByte) 1 else 0
        }
        maxOf(total.toDouble() / wholeSize, acc)
    }
}