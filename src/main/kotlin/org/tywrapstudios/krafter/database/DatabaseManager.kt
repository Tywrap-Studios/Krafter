package org.tywrapstudios.krafter.database

import kotlinx.coroutines.*
import org.jetbrains.exposed.v1.core.SqlLogger
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.core.statements.StatementContext
import org.jetbrains.exposed.v1.core.statements.expandArgs
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource
import org.tywrapstudios.krafter.LOGGING
import org.tywrapstudios.krafter.getDataDirectory
import javax.sql.DataSource
import kotlin.io.path.pathString

const val CONNECTION = "krafter.sqlite"

object DatabaseManager {

	private lateinit var database: Database
	val databaseType: String
		get() = database.dialect.name

	private var databaseContext = Dispatchers.IO + CoroutineName("Krafter Database")
	val krafterSqlLogger = object : SqlLogger {
		override fun log(context: StatementContext, transaction: Transaction) {
			LOGGING.literalDebug("[SQL]: ${context.expandArgs(transaction)}")
		}
	}

	@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
	fun setup(dataSource: DataSource?) {
		if (dataSource == null) {
			database = Database.connect(getDefaultDatasource())
			databaseContext = newSingleThreadContext("Krafter Database")
		} else {
			database = Database.connect(dataSource)
		}

		TransactionManager.defaultDatabase = database
	}

	private fun getDefaultDatasource(): DataSource {
		val dbFilepath = getDataDirectory().resolve(CONNECTION).pathString
		return SQLiteDataSource(
			SQLiteConfig().apply {
				setJournalMode(SQLiteConfig.JournalMode.WAL)
			}
		).apply {
			url = "jdbc:sqlite:$dbFilepath"
		}
	}
}
