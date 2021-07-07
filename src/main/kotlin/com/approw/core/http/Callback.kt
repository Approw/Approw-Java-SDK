package com.approw.core.http

import com.approw.core.graphql.GraphQLResponse.ErrorInfo

interface Callback<T> {
    fun onSuccess(result: T)
    fun onFailure(error: ErrorInfo?)
}