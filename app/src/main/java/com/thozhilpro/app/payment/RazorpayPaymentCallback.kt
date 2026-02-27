package com.thozhilpro.app.payment

interface RazorpayPaymentCallback {
    fun onPaymentSuccess(razorpayPaymentId: String, razorpayOrderId: String, razorpaySignature: String)
    fun onPaymentError(code: Int, description: String)
}
