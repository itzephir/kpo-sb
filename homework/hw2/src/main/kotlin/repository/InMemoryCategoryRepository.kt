package ru.hsebank.repository

import ru.hsebank.domain.repository.CategoryRepository
import ru.hsebank.models.Category
import ru.hsebank.models.Id
import ru.hsebank.storage.Categories

class InMemoryCategoryRepository(
    private val categories: Categories,
) : CategoryRepository {
    override suspend fun getCategories(): List<Category> =
        categories.values.values.toList()

    override suspend fun createCategory(category: Category): Boolean =
        categories.add(category)

    override suspend fun readCategoryById(id: Id): Category? =
        categories.get(id)

    override suspend fun updateCategoryById(id: Id, category: Category): Category? =
        categories.update(id, category)

    override suspend fun deleteCategoryById(id: Id): Category? =
        categories.remove(id)
}