package ru.hsebank.storage

import ru.hsebank.models.Category

class CategoriesVisitor : StorageVisitor<Category>

class Categories(categoriesVisitors: List<CategoriesVisitor>) : StorageVisitable<Category>(categoriesVisitors)
