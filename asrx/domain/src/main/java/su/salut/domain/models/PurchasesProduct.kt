package su.salut.domain.models

data class PurchasesProduct(
    var isPurchased: Boolean,
    var isPending: Boolean,
    val product: Product,
    val purchases: List<Purchase>
)
