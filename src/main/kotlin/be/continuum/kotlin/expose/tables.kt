package be.continuum.kotlin.expose

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Quotes : LongIdTable("quote") {
    val quoteNumber = varchar("quote_number", 25)

}

object Options : LongIdTable("option") {
    val quote = reference("quote_id", Quotes)
    val name = varchar("name", 25)
}


class Quote(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Quote>(Quotes)

    var quoteNumber by Quotes.quoteNumber
    val options by Option referrersOn Options.quote


}

class Option(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Option>(Options)

    var name by Options.name
    var quote by Options.quote
}


fun main() {
    Database.connect("jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user = "postgres", password = "postgres")
//    dslStyle()
    daoStyle()
}

private fun daoStyle() {
    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.drop(Quotes, Options)
        SchemaUtils.create(Quotes, Options)


        //val optionOne = transaction { listOf(Option.new { name = "leather" }, Option.new { name = "alcantara" }) }

        (0 until 15).forEach { nr ->

            val q = Quote.new {
                quoteNumber = nr.toString().padStart(5, '0')
            }
            Option.new {
                name = "leather-${nr}"
                quote = q.id
            }
            Option.new {
                name = "alcantara-${nr}"
                quote = q.id
            }


        }


        val selectAll = (Quotes innerJoin Options).selectAll()
        val resultSet = Quote.wrapRows(selectAll).toList()
        assert(resultSet.size == 15)

        val rangeRows = Quote.wrapRows(
            selectAll
                .andWhere { Between(Quotes.id, LiteralOp(LongColumnType(), 3), LiteralOp(LongColumnType(), 5)) })
            .toList()
        assert(rangeRows.size == 6)

        val singleQuote = Quote.wrapRows(
            (Quotes innerJoin Options).selectAll()
                .andWhere { Column<Long>(Quotes, "id", LongColumnType()).eq(LiteralOp(LongColumnType(), 3L)) })
            .toList()
        println("Quote with id 3 is: ${singleQuote}")

        println("Quotes: ${Quote.all().joinToString { it.id.value.toString() }}")
//        println("Users in ${stPete.name}: ${stPete.users.joinToString { it.name }}")
//        println("Adults: ${User.find { Users.age greaterEq 18 }.joinToString { it.name }}")
    }
}

private fun dslStyle() {

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.drop(Quotes, Options)
        SchemaUtils.create(Quotes, Options)
        (0 until 10).forEach { pk ->
            Options.insert {
//            it[id]. = pk
                it[name] = "leather-${id}"
            }
        }
    }

}
