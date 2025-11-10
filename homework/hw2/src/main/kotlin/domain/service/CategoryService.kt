package ru.hsebank.domain.service

import ru.hsebank.domain.repository.CategoryRepository
import ru.hsebank.models.Category
import ru.hsebank.models.Id

class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    suspend fun allCategories(): List<Category> = categoryRepository.getCategories()

    suspend fun createCategory(category: Category): Boolean {
        return categoryRepository.createCategory(category)
    }

    suspend fun findCategoryById(id: Id): Category? {
        return categoryRepository.readCategoryById(id)
    }

    suspend fun updateCategory(category: Category): Category? {
        val oldCategory = categoryRepository.readCategoryById(category.id) ?: return null
        if (oldCategory.type != category.type) return null
        return categoryRepository.updateCategoryById(category.id, category)
    }

    suspend fun deleteCategoryById(id: Id): Category? {
        return categoryRepository.deleteCategoryById(id)
    }
}