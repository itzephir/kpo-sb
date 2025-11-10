package ru.hsebank.di

import kotlinx.io.files.FileSystem
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.hsebank.controller.CommandLineController
import ru.hsebank.data.ReportRepository
import ru.hsebank.data.json.JsonReportRepository
import ru.hsebank.domain.repository.AccountRepository
import ru.hsebank.domain.repository.CategoryRepository
import ru.hsebank.domain.repository.OperationRepository
import ru.hsebank.domain.service.*
import ru.hsebank.json.json
import ru.hsebank.parser.ArgsParser
import ru.hsebank.parser.JsonArgsParser
import ru.hsebank.repository.InMemoryAccountRepository
import ru.hsebank.repository.InMemoryCategoryRepository
import ru.hsebank.repository.InMemoryOperationRepository
import ru.hsebank.storage.*

val appModule = module {
    factoryOf(::CommandLineController)
    singleOf(::JsonArgsParser) bind ArgsParser::class
    factoryOf(::AccountService)
    factoryOf(::CategoryService)
    factoryOf(::OperationService)
    factoryOf(::ReportService)
    singleOf(::InMemoryAccountRepository) bind AccountRepository::class
    singleOf(::InMemoryCategoryRepository) bind CategoryRepository::class
    single { OperationTransactionManager(get<InMemoryOperationRepository>(), get()) } bind OperationRepository::class
    singleOf(::InMemoryOperationRepository)
    singleOf(::LocalReportStorage) bind ReportStorage::class
    singleOf(::JsonReportRepository) bind ReportRepository::class
    single { Accounts(getAll<AccountsVisitor>()) }
    single { Categories(getAll<CategoriesVisitor>()) }
    single { Operations(getAll<OperationsVisitor>()) }

    singleOf(::AccountsVisitor) bind AccountsVisitor::class
    singleOf(::CategoriesVisitor) bind CategoriesVisitor::class
    singleOf(::OperationsVisitor) bind OperationsVisitor::class

    single { SystemFileSystem } bind FileSystem::class
    single { json } bind Json::class
}