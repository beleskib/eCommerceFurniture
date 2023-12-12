package com.example.ecommercefurniture.helper

fun Float?.getProductPrice(price: Float): Float {
    //percentage
    if(this == null)
        return price
    val remainingPricePercentage = 1.0f - this
    val priceAfterOffer = remainingPricePercentage * price

    return priceAfterOffer
}