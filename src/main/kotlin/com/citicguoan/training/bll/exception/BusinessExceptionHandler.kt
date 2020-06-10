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
internal open class BusinessExceptionHandler: DataFetcherExceptionHandler {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(BusinessExceptionHandler::class.java)
    }

    override fun onException(handlerParameters: DataFetcherExceptionHandlerParameters): DataFetcherExceptionHandlerResult {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.sourceLocation
        val path = handlerParameters.path

        val error: GraphQLError = GraphQLErrorImpl(
            exception = exception,
            locations = listOf(sourceLocation),
            path = path.toList()
        )
        LOGGER.warn(error.message, exception)
        return DataFetcherExceptionHandlerResult.newResult(error).build()
    }

    private class GraphQLErrorImpl(
        private val exception: Throwable,
        private val locations: List<SourceLocation>,
        private val path: List<Any>?,
        private val errorType: ErrorClassification = ErrorType.DataFetchingException
    ) : GraphQLError {
        override fun getErrorType(): ErrorClassification = errorType

        override fun getExtensions(): Map<String, Any> {
            if (exception !is BusinessException) {
                return emptyMap()
            }
            return mutableMapOf<String, Any>().apply {
                exception.fields?.let {
                    putAll(it)
                }
                this["code"] = "BUSINESS:${exception.code}"
            }
        }

        override fun getLocations(): List<SourceLocation> = locations

        override fun getMessage(): String = "Exception while fetching data (${path?.joinToString("/").orEmpty()}) : ${exception.message}"

        override fun getPath(): List<Any>? = path
    }

}