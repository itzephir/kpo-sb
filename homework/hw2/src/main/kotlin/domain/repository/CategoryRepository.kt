package ru.hsebank.domain.repository

import ru.hsebank.models.Category
import ru.hsebank.models.Id

interface CategoryRepository {
    suspend fun getCategories(): List<Category>

    suspend fun createCategory(category: Category): Boolean
    suspend fun readCategoryById(id: Id): Category?
    suspend fun updateCategoryById(id: Id, category: Category): Category?
    suspend fun deleteCategoryById(id: Id): Category?
}