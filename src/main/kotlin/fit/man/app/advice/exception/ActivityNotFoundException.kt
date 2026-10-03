package fit.man.app.advice.exception

class ActivityNotFoundException(
    message: String,
) : RuntimeException(ACTIVITY_NOT_FOUND_EXCEPTION_TEMPLATE.format(message)) {
    companion object {
        private const val serialVersionUID = -2071804204765069876L
        private const val ACTIVITY_NOT_FOUND_EXCEPTION_TEMPLATE = "ACTIVITY_NOT_FOUND_EXCEPTION: %s"
    }
}
