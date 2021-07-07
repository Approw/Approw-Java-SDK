package com.approw.core.graphql

import java.lang.Exception

class GraphQLException(message: String) : Exception("Graphql error: \n$message") {}
