package su.salut.data.lib.rustore

/**
 * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Error_Codes
 */
@Suppress("unused")
interface ResponseCode {
    companion object {
        const val OK = 0 // Successful request
        const val REQUEST_INVALID = 40001 // Request parameters are invalid - required parameters are not filled in/invalid parameter format
        const val APPLICATION_NOT_FOUND = 40003 // Application not found
        const val APPLICATION_INACTIVE = 40004 // Application status "inactive"
        const val PRODUCT_NOT_FOUND = 40005 // Product not found
        const val PRODUCT_INACTIVE = 40006 // Product status "inactive"
        const val INVALID_PRODUCT_TYPE = 40007 // Invalid product type. Supported types: "consumable", "non-consumable", "subscription"
        const val PURCHASE_EXISTS = 40008 // A purchase with this "order_id" already exists
        const val PRODUCT_INVOICE_CREATED = 40009 // Current customer found a purchase of this product with status "invoice_created". It is necessary to offer the client to pay / cancel the purchase
        const val PRODUCT_CONSUMABLE = 40010 // For product type "consumable". The current customer found a purchase of this product with the status "paid". You must first confirm the consumption of the purchase on the device, and then you can send the next request to purchase this product
        const val PRODUCT_NON_CONSUMABLE_CONFIRMED = 40011 // For product type "non-consumable". Current customer found a purchase of this product with the status "pre_confirmed"/"confirmed". This product has already been purchased. More than once the product is not sold
        const val PRODUCT_SUBSCRIPTION_CONFIRMED = 40012 // For product type "subscription". Current customer found a purchase of this product with the status "pre_confirmed"/"confirmed". This product has already been purchased. More than once the product is not sold
        const val PRODUCT_SUBSCRIPTION_GET_PRODUCTS = 40013 // For product type "subscription". When contacting the subscription service for a list of products "GET/products" ("serviceId", "user_id"), no data was received
        const val ATTRIBUTES_DID_NOT_COME_IN_REQUEST = 40014 // Required attribute(s) did not come in the request
        const val FAILED_CHANGE_STATUS = 40015 // Failed to change the status when updating the purchase (transition is prohibited)
        const val INVALID_QUANTITY = 40016 // Non-consumable product subscription purchase specified quantity > 1
        const val PRODUCT_REMOVED = 40017 // Product removed, no new purchases available
        const val INVALID_CONSUME_PRODUCT = 40018 // You cannot consume a product with type "product type"
        const val INVALID_TOKEN = 40101 // Invalid token
        const val TOKEN_EXPIRED = 40102 // Token expired
        const val ACCESS_DENIED = 40301 // Access to the requested resource is denied (unauthorized)
        const val TOKEN_NOT_AUTHORIZED = 40302 // For the current token, the current call is not authorized (method is forbidden)
        const val TOKEN_NOT_MATCH = 40303 // The application ID in the request and the token do not match
        const val INVALID_TOKEN_TYPE = 40305 // Invalid token type
        const val NOT_FOUND = 40401 // Not found
        const val TIMEOUT = 40801 // The notification timeout specified in the request has expired
        //50*** Payment service internal error

        const val PAYMENT_RESULT_INVOICE_RESULT = 33301 // payments ended with a result;
        const val PAYMENT_RESULT_INVALID_INVOICE = 33302 // payments ended without an invoice. They were probably launched with an incorrect invoice (an empty string, for example);
        const val PAYMENT_RESULT_PURCHASE_RESULT = 33303 // the result of the successful completion of the purchase of a digital product;
        const val PAYMENT_RESULT_INVALID_PURCHASE = 33304 // when paying for a digital product, payments ended with an error;
        const val PAYMENT_RESULT_INVALID_PAYMENT_STATE = 33305 // There is no PaymentState when completing payments.
        const val PAYMENT_RESULT_CLOSED_BY_USER = 33306 //
        const val PAYMENT_RESULT_TIMEOUT = 33307 //
        const val PAYMENT_RESULT_DECLINED_BY_SERVER = 33308 //
    }
}