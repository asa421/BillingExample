package su.salut.billingexample.domain.models

class BillingException(
    @BillingResultCode override val responseCode: Int,
    val debugMessage: String,
    val throwable: Throwable? = null
) : Exception(debugMessage), BillingResult {
    companion object {

        /** The request has reached the maximum timeout before Google Play responds. */
        fun getTimeoutException(
            debugMessage: String = "Data load timeout exception!",
            throwable: Throwable? = null
        ) = BillingException(
            responseCode = BillingResultCode.SERVICE_TIMEOUT,
            debugMessage = debugMessage,
            throwable = throwable
        )

        /** The requested feature is not supported by the Play Store on the current device. */
        fun getFeatureNotSupportedException(
            debugMessage: String = "Product type not supported for billing!",
            throwable: Throwable? = null
        ) = BillingException(
            responseCode = BillingResultCode.FEATURE_NOT_SUPPORTED,
            debugMessage = debugMessage,
            throwable = throwable
        )

        fun getErrorException(
            debugMessage: String = "Fatal error during the API action.",
            throwable: Throwable? = null
        ) = BillingException(
            responseCode = BillingResultCode.ERROR,
            debugMessage = debugMessage,
            throwable = throwable
        )
    }
}