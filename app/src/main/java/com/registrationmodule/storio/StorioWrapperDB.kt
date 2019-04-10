package com.registrationmodule.storio

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.WorkerThread
import com.pushtorefresh.storio3.Interceptor
import com.pushtorefresh.storio3.TypeMappingFinder
import com.pushtorefresh.storio3.internal.ChangesBus
import com.pushtorefresh.storio3.internal.Checks
import com.pushtorefresh.storio3.internal.Environment.RX_JAVA_2_IS_IN_THE_CLASS_PATH
import com.pushtorefresh.storio3.internal.InternalQueries.*
import com.pushtorefresh.storio3.internal.TypeMappingFinderImpl
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation
import com.pushtorefresh.storio3.sqlite.Changes
import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.impl.ChangesFilter
import com.pushtorefresh.storio3.sqlite.queries.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.*
import java.util.Collections.unmodifiableMap
import java.util.concurrent.atomic.AtomicInteger



/**
 * Default implementation of [StorIOSQLite] for [android.database.sqlite.SQLiteDatabase].
 *
 *
 * Thread-safe.
 */
class StorioWrapperDB protected constructor(
    private val sqLiteOpenHelper: SQLiteOpenHelper,
    typeMappingFinder: TypeMappingFinder,
    private val defaultRxScheduler: Scheduler?,
    interceptors: List<Interceptor>
) : StorIOSQLite() {

    private val changesBus = ChangesBus<Changes>(RX_JAVA_2_IS_IN_THE_CLASS_PATH)

    private val interceptors: List<Interceptor>

    /**
     * Implementation of [com.pushtorefresh.storio3.sqlite.StorIOSQLite.LowLevel].
     */
    private val lowLevel: StorIOSQLite.LowLevel

    init {
        this.interceptors = unmodifiableNonNullList<Interceptor>(interceptors)
        lowLevel = LowLevelImpl(typeMappingFinder)
    }

    /**
     * {@inheritDoc}
     */
    override fun observeChanges(backpressureStrategy: BackpressureStrategy): Flowable<Changes> {

        return changesBus.asFlowable()
            ?: throw IllegalStateException("Observing changes in StorIOSQLite requires RxJava")
    }

    /**
     * {@inheritDoc}
     */
    override fun observeChangesInTables(
        tables: Set<String>,
        backpressureStrategy: BackpressureStrategy
    ): Flowable<Changes> {
        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.applyForTables(observeChanges(backpressureStrategy), tables)
    }

    /**
     * {@inheritDoc}
     */
    override fun observeChangesOfTags(
        tags: Set<String>,
        backpressureStrategy: BackpressureStrategy
    ): Flowable<Changes> {
        // indirect usage of RxJava filter() required to avoid problems with ClassLoader when RxJava is not in ClassPath
        return ChangesFilter.applyForTags(observeChanges(backpressureStrategy), tags)
    }

    /**
     * {@inheritDoc}
     */
    override fun defaultRxScheduler(): Scheduler? {
        return defaultRxScheduler
    }

    /**
     * {@inheritDoc}
     */
    override fun lowLevel(): StorIOSQLite.LowLevel {
        return lowLevel
    }

    /**
     * {@inheritDoc}
     */
    override fun interceptors(): List<Interceptor> {
        return interceptors
    }

    /**
     * Closes underlying [SQLiteOpenHelper].
     *
     *
     * All calls to this instance of [StorIOSQLite]
     * after call to this method can produce exceptions
     * and undefined behavior.
     */
    @Throws(IOException::class)
    override fun close() {
        sqLiteOpenHelper.close()
    }

    /**
     * Builder for [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite].
     */
    class Builder
    /**
     * Please use [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.builder] instead of this.
     */
    internal constructor() {

        /**
         * Required: Specifies SQLite Open helper for internal usage.
         *
         *
         *
         * @param sqliteOpenHelper a SQLiteOpenHelper for internal usage.
         * @return builder.
         */
        fun sqliteOpenHelper(sqliteOpenHelper: SQLiteOpenHelper): StorioWrapperDB.CompleteBuilder {
            Checks.checkNotNull(sqliteOpenHelper, "Please specify SQLiteOpenHelper instance")
            return StorioWrapperDB.CompleteBuilder(sqliteOpenHelper)
        }
    }

    /**
     * Compile-time safe part of builder for [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite].
     */
    class CompleteBuilder internal constructor(private val sqLiteOpenHelper: SQLiteOpenHelper) {

        private var typeMapping: MutableMap<Class<*>, SQLiteTypeMapping<*>>? = null

        private var typeMappingFinder: TypeMappingFinder? = null

        private var defaultRxScheduler: Scheduler? = if (RX_JAVA_2_IS_IN_THE_CLASS_PATH) Schedulers.io() else null

        private val interceptors = ArrayList<Interceptor>()

        /**
         * Adds [SQLiteTypeMapping] for some type.
         *
         * @param type        type.
         * @param typeMapping mapping for type.
         * @param <T>         type.
         * @return builder.
        </T> */
        fun <T> addTypeMapping(type: Class<T>, typeMapping: SQLiteTypeMapping<T>): StorioWrapperDB.CompleteBuilder {
            Checks.checkNotNull(type, "Please specify type")
            Checks.checkNotNull(typeMapping, "Please specify type mapping")

            if (this.typeMapping == null) {
                this.typeMapping = HashMap()
            }

            this.typeMapping!![type] = typeMapping

            return this
        }

        /**
         * Optional: Specifies [TypeMappingFinder] for low level usage.
         *
         * @param typeMappingFinder non-null custom implementation of [TypeMappingFinder].
         * @return builder.
         */
        fun typeMappingFinder(typeMappingFinder: TypeMappingFinder): StorioWrapperDB.CompleteBuilder {
            Checks.checkNotNull(typeMappingFinder, "Please specify typeMappingFinder")

            this.typeMappingFinder = typeMappingFinder

            return this
        }

        /**
         * Optional: Specifies a scheduler on which [Flowable] / [io.reactivex.Single]
         * or [Comparable] will be subscribed.
         *
         *
         *
         * @return builder.
         * @see com.pushtorefresh.storio3.operations.PreparedOperation.asRxFlowable
         * @see com.pushtorefresh.storio3.operations.PreparedOperation.asRxSingle
         * @see PreparedCompletableOperation.asRxCompletable
         */
        fun defaultRxScheduler(defaultRxScheduler: Scheduler?): StorioWrapperDB.CompleteBuilder {
            this.defaultRxScheduler = defaultRxScheduler
            return this
        }

        /**
         * Optional: Adds [Interceptor] to all database operation.
         * Multiple interceptors would be called in the order they were added.
         *
         * @param interceptor non-null custom implementation of [Interceptor].
         * @return builder.
         */
        fun addInterceptor(interceptor: Interceptor): StorioWrapperDB.CompleteBuilder {
            interceptors.add(interceptor)
            return this
        }

        /**
         * Builds [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite] instance with required params.
         *
         * @return new [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite] instance.
         */
        fun build(): StorioWrapperDB {

            if (typeMappingFinder == null) {
                typeMappingFinder = TypeMappingFinderImpl()
            }
            if (typeMapping != null) {
                typeMappingFinder!!.directTypeMapping(unmodifiableMap<Class<*>, SQLiteTypeMapping<*>>(typeMapping))
            }

            return StorioWrapperDB(sqLiteOpenHelper, typeMappingFinder!!, defaultRxScheduler, interceptors)
        }
    }

    /**
     * {@inheritDoc}
     */
    protected inner class LowLevelImpl (private val typeMappingFinder: TypeMappingFinder) :
        StorIOSQLite.LowLevel() {

        private val lock = Any()

        private val numberOfRunningTransactions = AtomicInteger(0)

        /**
         * Guarded by [.lock].
         */
        private var pendingChanges: MutableSet<Changes> = HashSet(5)

        /**
         * Gets type mapping for required type.
         *
         *
         * This implementation can handle subclasses of types, that registered its type mapping.
         * For example: You've added type mapping for `User.class`,
         * and you have `UserFromServiceA.class` which extends `User.class`,
         * and you didn't add type mapping for `UserFromServiceA.class`
         * because they have same fields and you just want to have multiple classes.
         * This implementation will find type mapping of `User.class`
         * and use it as type mapping for `UserFromServiceA.class`.
         *
         * @return direct or indirect type mapping for passed type, or `null`.
         */
        override fun <T> typeMapping(type: Class<T>): SQLiteTypeMapping<T>? {
            return typeMappingFinder.findTypeMapping(type) as SQLiteTypeMapping<T>?
        }

        /**
         * Executes a single SQL statement that
         * is NOT a SELECT/INSERT/UPDATE/DELETE on the database.
         *
         *
         * Notice: Direct call of this method will not trigger notification from [RawQuery.affectsTables].
         * To send it use [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.executeSQL] instead.
         *
         * @param rawQuery sql query.
         */
        @WorkerThread
        override fun executeSQL(rawQuery: RawQuery) {
            if (rawQuery.args().isEmpty()) {
                sqLiteOpenHelper
                    .writableDatabase
                    .execSQL(rawQuery.query())
            } else {
                sqLiteOpenHelper
                    .writableDatabase
                    .execSQL(
                        rawQuery.query(),
                        rawQuery.args().toTypedArray()
                    )
            }
        }

        /**
         * Executes raw query on the database
         * and returns [android.database.Cursor] over the result set.
         *
         *
         * Notice: Direct call of this method will not trigger notification from [RawQuery.affectsTables].
         *
         * @param rawQuery sql query
         * @return A Cursor object, which is positioned before the first entry.
         * Note that Cursors are not synchronized, see the documentation for more details.
         */
        @WorkerThread
        override fun rawQuery(rawQuery: RawQuery): Cursor {
            return sqLiteOpenHelper
                .readableDatabase
                .rawQuery(
                    rawQuery.query(),
                    nullableArrayOfStrings(rawQuery.args())
                )
        }

        /**
         * {@inheritDoc}
         */
        @WorkerThread
        override fun query(query: Query): Cursor {
            return sqLiteOpenHelper
                .readableDatabase.query(
                query.distinct(),
                query.table(),
                nullableArrayOfStringsFromListOfStrings(query.columns()),
                nullableString(query.where()),
                nullableArrayOfStringsFromListOfStrings(query.whereArgs()),
                nullableString(query.groupBy()),
                nullableString(query.having()),
                nullableString(query.orderBy()),
                nullableString(query.limit())
            )
        }

        /**
         * Inserts a row into the database.
         *
         *
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with [.notifyAboutChanges]
         * or use [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.put] instead.
         *
         * @param insertQuery   query.
         * @param contentValues map that contains the initial column values for the row.
         * The keys should be the column names and the values the column values.
         * @return id of inserted row.
         */
        @WorkerThread
        override fun insert(insertQuery: InsertQuery, contentValues: ContentValues): Long {
            return sqLiteOpenHelper
                .writableDatabase
                .insertOrThrow(
                    insertQuery.table(),
                    insertQuery.nullColumnHack(),
                    contentValues
                )
        }

        /**
         * Inserts a row into the database.
         *
         *
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with [.notifyAboutChanges]
         * or use [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.put] instead.
         *
         * @param insertQuery       query.
         * @param contentValues     map that contains the initial column values for the row.
         * The keys should be the column names and the values the column values.
         * @param conflictAlgorithm for insert conflict resolver.
         * @return the row ID of the newly inserted row OR the primary key of the existing row
         * if the input param 'conflictAlgorithm' = [android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE]
         * OR -1 if any error.
         * @see android.database.sqlite.SQLiteDatabase.insertWithOnConflict
         * @see android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
         *
         * @see android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
         *
         * @see android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL
         *
         * @see android.database.sqlite.SQLiteDatabase.CONFLICT_ROLLBACK
         *
         * @see android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
         */
        @WorkerThread
        override fun insertWithOnConflict(
            insertQuery: InsertQuery,
            contentValues: ContentValues,
            conflictAlgorithm: Int
        ): Long {
            return sqLiteOpenHelper
                .writableDatabase
                .insertWithOnConflict(
                    insertQuery.table(),
                    insertQuery.nullColumnHack(),
                    contentValues,
                    conflictAlgorithm
                )
        }

        /**
         * Updates one or multiple rows in the database.
         *
         *
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with [.notifyAboutChanges]
         * or use [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.put] instead.
         *
         * @param updateQuery   query.
         * @param contentValues a map from column names to new column values.
         * `null` is a valid value that will be translated to `NULL`.
         * @return the number of rows affected.
         */
        @WorkerThread
        override fun update(updateQuery: UpdateQuery, contentValues: ContentValues): Int {
            return sqLiteOpenHelper
                .writableDatabase
                .update(
                    updateQuery.table(),
                    contentValues,
                    nullableString(updateQuery.where()),
                    nullableArrayOfStringsFromListOfStrings(updateQuery.whereArgs())
                )
        }

        /**
         * Deletes one or multiple rows in the database.
         *
         *
         * Notice: Direct call of this method will not trigger notification,
         * you should do it manually with [.notifyAboutChanges]
         * or use [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.delete] instead.
         *
         * @param deleteQuery query.
         * @return the number of rows deleted.
         */
        @WorkerThread
        override fun delete(deleteQuery: DeleteQuery): Int {
            return sqLiteOpenHelper
                .writableDatabase
                .delete(
                    deleteQuery.table(),
                    nullableString(deleteQuery.where()),
                    nullableArrayOfStringsFromListOfStrings(deleteQuery.whereArgs())
                )
        }

        /**
         * {@inheritDoc}
         */
        override fun notifyAboutChanges(changes: Changes) {
            Checks.checkNotNull(changes, "Changes can not be null")

            // Fast path, no synchronization required
            if (numberOfRunningTransactions.get() == 0) {
                changesBus.onNext(changes)
            } else {
                synchronized(lock) {
                    pendingChanges.add(changes)
                }

                notifyAboutPendingChangesIfNotInTransaction()
            }
        }

        private fun notifyAboutPendingChangesIfNotInTransaction() {
            val changesToSend: Set<Changes>?

            if (numberOfRunningTransactions.get() == 0) {
                synchronized(lock) {
                    changesToSend = pendingChanges
                    pendingChanges = HashSet(5)
                }
            } else {
                changesToSend = null
            }

            if (changesToSend != null && changesToSend.size > 0) {
                val affectedTables = HashSet<String>(3)
                val affectedTags = HashSet<String>(3)
                for (changes in changesToSend) {
                    // Merge all changes into one Changes object.
                    affectedTables.addAll(changes.affectedTables())
                    affectedTags.addAll(changes.affectedTags())
                }
                changesBus.onNext(Changes.newInstance(affectedTables, affectedTags))
            }
        }

        /**
         * {@inheritDoc}
         */
        override fun beginTransaction() {
            sqLiteOpenHelper
                .writableDatabase
                .beginTransaction()

            numberOfRunningTransactions.incrementAndGet()
        }

        /**
         * {@inheritDoc}
         */
        override fun setTransactionSuccessful() {
            sqLiteOpenHelper
                .writableDatabase
                .setTransactionSuccessful()
        }

        /**
         * {@inheritDoc}
         */
        override fun endTransaction() {
            sqLiteOpenHelper
                .writableDatabase
                .endTransaction()

            numberOfRunningTransactions.decrementAndGet()
            notifyAboutPendingChangesIfNotInTransaction()
        }

        /**
         * {@inheritDoc}
         */
        override fun sqliteOpenHelper(): SQLiteOpenHelper {
            return sqLiteOpenHelper
        }
    }

    companion object {

        /**
         * Creates new builder for [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite].
         *
         * @return non-null instance of [com.pushtorefresh.storio3.sqlite.impl.DefaultStorIOSQLite.Builder].
         */
        fun builder(): StorioWrapperDB.Builder {
            return StorioWrapperDB.Builder()
        }
    }
}
