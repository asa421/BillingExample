package su.salut.domain.models

/** Represents the details of a one time or subscription product. */
interface Product {
    /** The product's Id. */
    val productId: String

    /** The title of the product being sold. */
    val title: String

    /** The description of the product. */
    val description: String

    /** Formatted price of the item, including its currency sign. */
    val priceLabel: String

    /**
     *  Returns price in micro-units, where 1,000,000 micro-units equal one unit of the currency.
     *
     *  For example, if price is "€7.99", price_amount_micros is "7990000".
     *  This value represents the localized, rounded price for a particular currency.
     */
    val priceAmountMicros: Long

    /** ISO 4217 currency code for price and original price. */
    val priceCurrencyCode: String
}