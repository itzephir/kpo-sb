package ru.hsebank.builder

import ru.hsebank.models.Account
import ru.hsebank.models.Category
import ru.hsebank.models.Operation
import ru.hsebank.models.Report
import kotlin.time.Clock
import kotlin.time.Instant


class ReportBuilder(
    private val date: Instant = Clock.System.now(),
) {
    private var categories: Set<Category> = emptySet()
    private var accountsAndOperations: Map<Account, List<Operation>> = emptyMap()

    fun withCategories(categories: List<Category>): ReportBuilder {
        return this.apply { this.categories = categories.toSet() }
    }

    fun withAccountsAndOperations(accountsAndOperations: Map<Account, List<Operation>>): ReportBuilder {
        return this.apply {
            this.accountsAndOperations = accountsAndOperations
        }
    }

    fun build(): Report = Report(
        date = date,
        categories = categories.toList(),
        accountsAndOperations = accountsAndOperations
    )
}