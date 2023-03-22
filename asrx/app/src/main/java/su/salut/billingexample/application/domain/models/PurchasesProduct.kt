package su.salut.billingexample.application.domain.models

data class PurchasesProduct(
    var isPurchased: Boolean,
    var isPending: Boolean,
    var isAcknowledged: Boolean,
    val product: Product,
    val purchases: List<Purchase>
)
