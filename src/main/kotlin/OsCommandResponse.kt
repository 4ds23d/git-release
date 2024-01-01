package org.example

class OsCommandResponse(val response: List<String>, val statusCode: Int)  {

    override fun toString(): String {
        return "Status: ${statusCode}\n $response"
    }
}