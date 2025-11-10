package ru.hsebank.controller

import kotlinx.coroutines.flow.toList
import ru.hsebank.data.ReportRepository
import ru.hsebank.domain.service.AccountService
import ru.hsebank.domain.service.CategoryService
import ru.hsebank.domain.service.OperationService
import ru.hsebank.domain.service.ReportService
import ru.hsebank.parser.ArgsParser
import ru.hsebank.parser.ArgsParser.Companion.parse
import kotlin.time.Instant

class CommandLineController(
    private val accountService: AccountService,
    private val categoryService: CategoryService,
    private val operationService: OperationService,
    private val reportService: ReportService,
    private val reportRepository: ReportRepository,
    private val argsParser: ArgsParser,
) {
    suspend fun processCommand(command: String, args: String): String {
        return when (command) {
            "help"             -> printHelp()
            "get-accounts"     -> accountService.allAccounts().toList().sortedBy { it.name.value }.toString()
            "create-account"   -> createAccount(args)
            "get-account"      -> getAccount(args)
            "modify-account"   -> modifyAccount(args)
            "delete-account"   -> deleteAccount(args)
            "get-categories"   -> categoryService.allCategories().toList().sortedWith { lhs, rhs ->
                if (lhs.type == rhs.type) {
                    lhs.name.value.compareTo(rhs.name.value)
                } else {
                    lhs.type.compareTo(rhs.type)
                }
            }.toString()

            "create-category"  -> createCategory(args)
            "get-category"     -> getCategory(args)
            "modify-category"  -> modifyCategory(args)
            "delete-category"  -> deleteCategory(args)
            "get-operations"   -> operationService.allOperations().toList().sortedBy { it.date }.toString()
            "create-operation" -> createOperation(args)
            "get-operation"    -> getOperation(args)
            "modify-operation" -> modifyOperation(args)
            "delete-operation" -> deleteOperation(args)
            "print-report"     -> printReport(args)
            "save-report"      -> saveReport(args)
            "load-report"      -> loadReport(args)
            "exit"             -> "exit"
            else               -> error("unknown command")
        }
    }

    private fun printHelp(): String {
        return """
            help - print this help message
            create-account - create account, input: json of account
            get-account - get account by id, input: json of id
            modify-account - modify account by id, input: json of account
            delete-account - delete account by id, input: json of id
            create-category - create category, input: json of category
            get-category - get category by id, input: json of id
            modify-category - modify category by id, input: json of category
            delete-category - delete category by id, input: json of id
            create-operation - create operation, input: json of operation
            get-operation - get operation by id, input: json of id
            modify-operation - modify operation by id, input: json of operation
            delete-operation - delete operation by id, input: json of id
            print-report - print report, input: json of report
            save-report - save report, input: json of report
            load-report - load report, input: json of report
        """.trimIndent()
    }

    private suspend fun createAccount(args: String): String =
        if (accountService.createAccount(argsParser.parse(args))) {
            "Account was successfully created"
        } else {
            "Account was not created"
        }

    private suspend fun getAccount(args: String): String {
        return when (val account = accountService.findAccountById(argsParser.parse(args))) {
            null -> "No account was found"
            else -> account.toString()
        }
    }

    private suspend fun modifyAccount(args: String): String {
        return when (val oldAccount = accountService.updateAccount(argsParser.parse(args))) {
            null -> "No account was modified"
            else -> oldAccount.toString()
        }
    }

    private suspend fun deleteAccount(args: String): String {
        return when (val oldAccount = accountService.deleteAccountById(argsParser.parse(args))) {
            null -> "No account was deleted"
            else -> oldAccount.toString()
        }
    }

    private suspend fun createCategory(args: String): String =
        if (categoryService.createCategory(argsParser.parse(args))) {
            "Category was successfully created"
        } else {
            "Category was not created"
        }

    private suspend fun getCategory(args: String): String {
        return when (val category = categoryService.findCategoryById(argsParser.parse(args))) {
            null -> "No category was found"
            else -> category.toString()
        }
    }

    private suspend fun modifyCategory(args: String): String {
        return when (val oldCategory = categoryService.updateCategory(argsParser.parse(args))) {
            null -> "No category was modified"
            else -> oldCategory.toString()
        }
    }

    private suspend fun deleteCategory(args: String): String {
        return when (val oldCategory = categoryService.deleteCategoryById(argsParser.parse(args))) {
            null -> "No category was deleted"
            else -> oldCategory.toString()
        }
    }

    private suspend fun createOperation(args: String): String =
        if (operationService.createOperation(argsParser.parse(args))) {
            "Operation was successfully created"
        } else {
            "Operation was not created"
        }

    private suspend fun getOperation(args: String): String {
        return when (val operation = operationService.findOperationById(argsParser.parse(args))) {
            null -> "No operation was found"
            else -> operation.toString()
        }
    }

    private suspend fun modifyOperation(args: String): String {
        return when (val oldOperation = operationService.updateOperation(argsParser.parse(args))) {
            null -> "No operation was modified"
            else -> oldOperation.toString()
        }
    }

    private suspend fun deleteOperation(args: String): String {
        return when (val oldOperation = operationService.deleteOperationById(argsParser.parse(args))) {
            null -> "No operation was deleted"
            else -> oldOperation.toString()
        }
    }

    private suspend fun printReport(args: String): String {
        val date = args.takeIf { it.isNotEmpty() }?.let { Instant.parse(it) }
        val report = reportService.generateReport(date)
        return report.toString()
    }

    private suspend fun saveReport(args: String): String {
        val date = Instant.parseOrNull(args)
        val report = reportService.generateReport(date)
        if (!reportService.saveReport(report)) {
            return "Report was not saved"
        }
        return "Report was successfully saved: ${report.date}.${reportRepository.format}"
    }

    private suspend fun loadReport(args: String): String {
        val date = Instant.parseOrNull(args) ?: return "No date"
        val report = reportService.loadReport(date) ?: return "No report"
        return report.toString()
    }
}