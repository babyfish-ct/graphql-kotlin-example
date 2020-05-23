package com.citicguoan.training.bll.exception

import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import graphql.language.SourceLocation
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
internal open class BusinessDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BusinessDataFetcherExceptionHandler::class.java)
    }

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val error: GraphQLError = GraphQLErrorImpl(
            exception,
            handlerParameters.sourceLocation,
            handlerParameters.path.toList()
        )
        LOGGER.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult(error).build()
    }

    private inner class GraphQLErrorImpl(
        private val exception: Throwable,
        private val sourceLocation: SourceLocation,
        private val path: MutableList<Any>
    ) : GraphQLError {

        override fun getMessage(): String = exception.message ?: ""

        override fun getPath(): MutableList<Any> = path

        override fun getErrorType(): ErrorClassification =
            if (exception is BusinessException) {
                ErrorCode(exception.code)
            } else {
                ErrorType.DataFetchingException
            }

        override fun getExtensions(): MutableMap<String, Any> =
            if (exception is BusinessException) {
                LinkedHashMap(exception.fields)
            } else {
                mutableMapOf<String, Any>().apply {
                    if (exception is GraphQLError && exception.extensions != null) {
                        putAll(exception.extensions)
                    }
                }
            }

        override fun getLocations(): MutableList<SourceLocation> =
            mutableListOf(sourceLocation)
    }

    private class ErrorCode(val code: String): ErrorClassification {
        override fun toString(): String = code
    }
}