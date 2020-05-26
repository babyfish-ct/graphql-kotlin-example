package com.citicguoan.training.loader.common

import org.dataloader.DataLoaderOptions
import org.springframework.transaction.PlatformTransactionManager

/**
 * This is abstract data loader for the object property that is not collection, such as
 * 1. many-to-one/one-to-one association property
 * 2. expensive scalar property
 *
 * ClassName suggestion for the derived classes:
 * {LoadedDataTypeName}Loader
 * , like 'DepartmentLoader' of this demo
 *
 * @param <K> The id(primary key) type of the loaded object
 * @param <V> the loaded object type
 */
abstract class AbstractValueLoader<K, V>(
    transactionManager: PlatformTransactionManager?,

    /*
     * the batchGetter, get the values by some id(primary key)s
     *
     * Be different with org.dataloader.BatchLoader,
     * the size of returned collection can be less than the size of argument collection
     */
    batchGetter: (Collection<K>) -> Collection<V>,

    /*
     * How to get the id(primary key) from value object,
     * like Department::id of this demo
     */
    keyGetter: (V) -> K,

    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
) : AbstractDataLoader<K, V?>(
    transactionManager,
    { keys ->
        batchGetter(keys)
            .associateBy(keyGetter)
            .let { map ->
                keys.map { map[it] }
            }
        /*
         * For example:
         *
         * 1. Prepare step:
         * employees:
         * [
         *     { id1, departmentId1},
         *     { id2, departmentId1},
         *     { id2, departmentId1},
         *     { id4, departmentId2},
         *     { id5, departmentId2},
         *     { id2, departmentId2},
         *     { id7, departmentId3}
         * ]
         *
         *
         * 2. Get the parameter 'keys':
         *
         * keys is the department id collection
         * [ departmentId1, departmentId2, departmentId3]
         *
         * The loaded object type is Department,
         * so <K> is the id(primary key) type of loaded object type
         *
         *
         * 3. .batchGetter(keys):
         *
         * this method returns
         * [ departmentObj1, departmentObj2 ]
         * Only two department objects are loaded because there is not data for departmentId3
         *
         *
         * 4. .associateBy(keyGetter):
         *
         * this methods returns a Map<Long, Department>, like this
         * {
         *     key1: departmentObj1, // key1 == departmentId1
         *     kye2: departmentObj2  // key2 == departmentId2
         * }
         *
         *
         * 5: keys.map { map[it] }:
         *
         * return the collection for org.dataloader.BatchLoader,
         * the size of returned collection must be same with the size of keys
         *
         * like this:
         * [
         *      departmentObj1, // for key1
         *      departmentObj2, // for key2
         *      null            // for key3
         * ]
         */
    },
    optionsInitializer
)