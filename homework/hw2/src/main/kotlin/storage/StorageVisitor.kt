package ru.hsebank.storage

import ru.hsebank.models.Identifiable

interface StorageVisitor<T : Identifiable> {
    fun visitAdd(value: T?) {}
    fun visitGet(value: T?) {}
    fun visitUpdate(oldValue: T?, newValue: T?) {}
    fun visitRemove(value: T?) {}
}