package fit.man.app.advice.exception

class FitFileException : RuntimeException {
    constructor(message: String) : super(FIT_FILE_EXCEPTION_TEMPLATE.format(message))
    constructor(message: String, cause: Throwable) : super(FIT_FILE_EXCEPTION_TEMPLATE.format(message), cause)

    companion object {
        private const val serialVersionUID = -6235884954053644325L
        private const val FIT_FILE_EXCEPTION_TEMPLATE = "FIT_FILE_EXCEPTION: %s"
    }
}
