package com.example.goshop.helper

fun Float?.getProductPrice(price: Float): Float {
    //this --> Percentage
    if (this == null)
        return price
    val remainingPricePercentage = 1f - this

    return remainingPricePercentage * price
}