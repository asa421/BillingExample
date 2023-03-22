package su.salut.billingexample.application.domain.models

/** Represents an in-app billing purchase. */
interface Purchase {
    /** Token that uniquely identifies a purchase for a given item and user pair. */
    val purchaseToken: String

    /** The product Ids */
    val productIds: List<String>

    /** Indicates if purchased. */
    val isPurchased: Boolean

    /** Indicates if pending. */
    val isPending: Boolean

    /** Indicates whether a trial period is in progress. */
    val isTrialPeriod: Boolean

    /** Indicates whether the purchase has been acknowledged. */
    val isAcknowledged: Boolean
}
