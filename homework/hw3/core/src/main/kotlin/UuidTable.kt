package ru.hse.core

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.InternalApi
import org.jetbrains.exposed.v1.core.Table.Dual.clientDefault
import org.jetbrains.exposed.v1.core.Table.Dual.registerColumn
import org.jetbrains.exposed.v1.core.statements.api.RowApi
import org.jetbrains.exposed.v1.core.transactions.currentTransaction
import org.jetbrains.exposed.v1.core.vendors.DataTypeProvider
import org.jetbrains.exposed.v1.core.vendors.H2Dialect
import org.jetbrains.exposed.v1.core.vendors.MariaDBDialect
import org.jetbrains.exposed.v1.core.vendors.currentDialect
import java.nio.ByteBuffer
import kotlin.uuid.Uuid

class UuidColumnType : ColumnType<Uuid>() {
    override fun sqlType(): String = currentDialect.dataTypeProvider.uuidType()

    override fun valueFromDB(value: Any): Uuid = when (value) {
        is Uuid                                -> value

        is ByteArray                           -> ByteBuffer.wrap(value)
            .let { b -> Uuid.fromLongs(b.long, b.long) }

        is String if value.matches(uuidRegexp) -> Uuid.parse(value)

        is String                              -> ByteBuffer.wrap(value.toByteArray())
            .let { b -> Uuid.fromLongs(b.long, b.long) }

        is ByteBuffer                          -> value
            .let { b -> Uuid.fromLongs(b.long, b.long) }

        else                                   ->
            error("Unexpected value of type Uuid: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Uuid): Any {
        return ((currentDialect as? H2Dialect)?.originalDataTypeProvider ?: currentDialect.dataTypeProvider)
            .uuidToDB(value)
    }

    @Suppress("MagicNumber")
    private fun DataTypeProvider.uuidToDB(value: Uuid): Any =
        ByteBuffer.allocate(16)
            .putLong(value.toLongs { mostSignificantBits, _ -> mostSignificantBits })
            .putLong(value.toLongs { _, leastSignificantBits -> leastSignificantBits }).array()

    override fun nonNullValueToString(value: Uuid): String = "'$value'"

    @Suppress("MagicNumber")
    override fun readObject(rs: RowApi, index: Int): Any? {
        @OptIn(InternalApi::class)
        val db = currentTransaction().db
        if (currentDialect is MariaDBDialect && !db.version.covers(10)) {
            return rs.getObject(index, java.sql.Array::class.java)
        }
        return super.readObject(rs, index)
    }

    companion object {
        private val uuidRegexp =
            Regex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}", RegexOption.IGNORE_CASE)
    }
}

fun kotlinUuid(name: String = ""): Column<Uuid> = registerColumn(name, UuidColumnType())

fun Column<Uuid>.autoGenerate(): Column<Uuid> = clientDefault { Uuid.random() }