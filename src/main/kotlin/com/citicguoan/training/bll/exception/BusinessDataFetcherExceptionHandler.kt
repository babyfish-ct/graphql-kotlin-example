package com.citicguoan.training.bll.exception

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
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
                BusinessErrorType(exception.code)
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

    /*
     * GraphQLError will be sent to client, client can access property 'errorType'
     * 1. If it's a string, that means generic exception
     * 2. If it's a object with property 'code', that means business exception
     */
    @JsonSerialize(using = BusinessErrorTypeSerializer::class)
    private class BusinessErrorType(val code: String): ErrorClassification

    private class BusinessErrorTypeSerializer: JsonSerializer<BusinessErrorType>() {
        override fun serialize(
            value: BusinessErrorType,
            gen: JsonGenerator,
            serializers: SerializerProvider) {
            gen.writeString("BUSINESS:${value.code}")
        }
    }
}