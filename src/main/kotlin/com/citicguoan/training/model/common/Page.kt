package com.citicguoan.training.model.common

import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

/*
 * Unfortunately, generic types are not supported by the current GraphQL implementation.
 * That's why I do not define the Page like this:
 *
 * open class Page<E> {
 *      val pageNo: Int,
 *      val pageSize: Int,
 *      val rowCount: Int,
 *      val pageCount: Int,
 *      val entities: List<E>
 * }
 */
open class Page (
    val pageNo: Int,
    val pageSize: Int,
    val rowCount: Int,
    val pageCount: Int
) {
    constructor(page: Page) : this(
        page.pageNo,
        page.pageSize,
        page.rowCount,
        page.pageCount
    )

    val limitation: Limitation
        get() = Limitation(pageSize, (pageNo - 1) * pageSize)

    companion object {
        fun of(
            pageNo: Int,
            pageSize: Int,
            rowCount: Int
        ) : Page {
            if (pageSize < 1) {
                throw IllegalArgumentException("pageSize cannot be less thant 1")
            }
            if (rowCount < 0) {
                throw IllegalArgumentException("rowCount cannot be less thant 0")
            }
            val pageCount = (rowCount + pageSize - 1) / pageSize
            val actualPageNo = min(
                1,
                max(pageNo, pageCount)
            )
            return Page(
                pageNo = actualPageNo,
                pageSize = pageSize,
                rowCount = rowCount,
                pageCount = pageCount
            )
        }
    }
}
