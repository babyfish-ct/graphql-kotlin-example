package com.citicguoan.training.loader.common

import org.dataloader.DataLoaderOptions
import org.springframework.transaction.PlatformTransactionManager

/**
 * This is abstract data loader for the object property that is collection,
 * that is only used for one-to-many/many-to-many association
 *
 * ClassName suggestion for the derived classes:
 * {LoadedDataTypeName}ListBy{ForeignKey}Loader
 * , like
 * 'EmployeeListByDepartmentIdLoader',
 * 'EmployeeListBySupervisorIdLoader' of this demo
 *
 * @param <K> The foreign key type of the loaded object
 * @param <E> the element type of loaded collection
 */
abstract class AbstractListLoader<K, E>(
    transactionManager: PlatformTransactionManager?,

    /*
     * the batchGetter, get the values by some foreign keys
     *
     * Be different with org.dataloader.BatchLoader,
     * the size of returned collection can be less than the size of argument collection
     */
    batchGetter: (Collection<K>) -> Collection<E>,

    /*
     * How to get the foreign key from value object,
     * like 'Employee::departmentId' and 'Employee::supervisorId' of this demo
     */
    keyGetter: (E) -> K?,

    optionsInitializer: (DataLoaderOptions.() -> Unit)? = null
): AbstractDataLoader<K, List<E>>(
    transactionManager,
    { keys ->
        batchGetter(keys)
            .groupBy(keyGetter)
            .let { map ->
                keys.map { map[it] ?: emptyList() }
            }

        /*
         * For example:
         *
         * 1. Prepare step:
         * department:
         * [
         *     { id1 },
         *     { id2 },
         *     { id3 }
         * ]
         *
         *
         * 2. Get the parameter 'keys':
         *
         * keys is the id collection
         * [ id1, id2, id3 ]
         *
         * The loaded object type is Employee,
         * so <K> is the foreign key type of loaded object type
         *
         *
         * 3. .batchGetter(keys):
         *
         * this method returns
         * [
         *     employeeObj1, employeeObj2, employeeObj3, // 3 employee objects for key1 (first foreign key)
         *     employeeObj4, employeeObj5, employeeObj6  // 3 employee objects for key2 (second foreign key)
         *                                               // No objects for key3(third foreign key)
         *  ]
         *
         *
         * 4. keys.map { map[it] ?: emptyList() }:
         *
         * this methods returns a Map<Long, List<Employee>> like this
         * [
         *     key1: [employeeObj1, employeeObj2, employeeObj3]
         *     key2: [employeeObj4, employeeObj5, employeeObj6]
         * ]
         *
         *
         * 5. keys.map { map[it] ?: emptyList() }:
         *
         * return the collection for org.dataloader.BatchLoader, like this
         * [
         *     [employeeObj1, employeeObj2, employeeObj3] // for key1
         *     [employeeObj4, employeeObj5, employeeObj6] // for key2
         *     []                                         // for key3
         * ]
         * the size of returned collection must be same with the size of keys
         */
    },

    optionsInitializer
)