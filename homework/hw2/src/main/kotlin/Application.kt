package ru.hsebank

import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import ru.hsebank.controller.CommandLineController
import ru.hsebank.di.appModule

class Application : KoinComponent {

    suspend fun run(controller: CommandLineController = get()) {
        while (true) {
            println("enter command")
            val line = readln()
            val (command, args) = line.split(" ").let { it[0] to it.drop(1).joinToString(" ") }
            val output = try {
                controller.processCommand(command, args)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            if (output == "exit") {
                break
            } else {
                println(output)
            }
        }
    }

    companion object {
        suspend fun main() {
            startKoin {
                modules(appModule)
            }
            Application().run()
        }
    }
}

suspend fun main() = Application.main()
