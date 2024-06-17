package zzibu.jeho.tagify.exception

data class ErrorResponse(val code : ErrorCode, val details : String, val timestamp: String, val path : String)
{}