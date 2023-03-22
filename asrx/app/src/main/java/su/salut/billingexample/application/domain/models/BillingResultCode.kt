package su.salut.billingexample.application.domain.models

@Suppress("unused")
annotation class BillingResultCode {
    companion object {
        const val SERVICE_TIMEOUT = -3 // The request has reached the maximum timeout before Google Play responds.
        const val FEATURE_NOT_SUPPORTED = -2 // The requested feature is not supported by the Play Store on the current device.
        const val SERVICE_DISCONNECTED = -1 // The app is not connected to the Play Store service via the Google Play Billing Library.
        const val OK = 0 // Success.
        const val USER_CANCELED = 1 // Transaction was canceled by the user.
        const val SERVICE_UNAVAILABLE = 2 // The service is currently unavailable.
        const val BILLING_UNAVAILABLE = 3 // A user billing error occurred during processing.
        const val ITEM_UNAVAILABLE = 4 // The requested product is not available for purchase.
        const val DEVELOPER_ERROR = 5 // Error resulting from incorrect usage of the API.
        const val ERROR = 6 // Fatal error during the API action.
        const val ITEM_ALREADY_OWNED = 7 // The purchase failed because the item is already owned.
        const val ITEM_NOT_OWNED = 8 // Requested action on the item failed since it is not owned by the user.
    }
}
