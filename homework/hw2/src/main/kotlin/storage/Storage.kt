package ru.hsebank.storage

import ru.hsebank.models.Id
import ru.hsebank.models.Identifiable

abstract class StorageVisitable<T : Identifiable>(
    private val visitors: List<StorageVisitor<T>>,
) : Storage<T>() {
    override fun add(value: T): Boolean =
        super.add(value)
            .also { added -> visitors.forEach { it.visitAdd(value.takeIf { added }) } }

    override fun get(id: Id): T? =
        super.get(id)
            .also { value -> visitors.forEach { it.visitGet(value) } }

    override fun update(id: Id, value: T): T? =
        super.update(id, value)
            .also { oldValue -> visitors.forEach { it.visitUpdate(oldValue, value) } }

    override fun remove(id: Id): T? =
        super.remove(id)
            .also { oldValue -> visitors.forEach { it.visitRemove(oldValue) } }
}

abstract class Storage<T : Identifiable> {
    val values = mutableMapOf<Id, T>()

    open fun add(value: T): Boolean =
        (if (value.id in values) false
        else values.put(value.id, value) == null)

    open fun get(id: Id): T? =
        values[id]

    open fun update(id: Id, value: T): T? =
        (if (id in values) values.put(id, value) else null)

    open fun remove(id: Id): T? =
        values.remove(id)
}