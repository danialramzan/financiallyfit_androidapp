package com.danrmzn.financiallyfit

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PaymentScreen() {
    val paymentSheet = rememberPaymentSheet(::onPaymentSheetResult)
    val context = LocalContext.current
    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }
    val user = FirebaseAuth.getInstance().currentUser


    LaunchedEffect(context) {

        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = result.token


            // Call the Firebase Cloud Function to get Stripe PaymentIntent details
            "https://us-central1-financiallyfit-52b32.cloudfunctions.net/payment_sheet"  // Replace with actual backend URL
                .httpPost()
                .header("Authorization",
//                    "Bearer $token"
                    "Bearer $token"
                )
                .responseJson { _, _, result ->
                    Log.wtf("GNX", token)
                    if (result is Result.Success) {
                        Log.wtf("GNX", "Success!!!!")
                        val responseJson = result.get().obj()
                        paymentIntentClientSecret = responseJson.getString("paymentIntent")
                        Log.wtf("GNX", paymentIntentClientSecret)
                        customerConfig = PaymentSheet.CustomerConfiguration(
                            id = responseJson.getString("customer"),
                            ephemeralKeySecret = responseJson.getString("ephemeralKey")
                        )
                        val publishableKey = responseJson.getString("publishableKey")
                        PaymentConfiguration.init(context, publishableKey)
                    } else
                        Log.wtf("GNX", "NOT SUCCESS!!!!")
                }
        }
    }

    Button(
        onClick = {
            val currentConfig = customerConfig
            val currentClientSecret = paymentIntentClientSecret
            Log.d("GNX", customerConfig.toString())
            Log.d("GNX", paymentIntentClientSecret.toString())
            if (currentConfig != null && currentClientSecret != null) {
                presentPaymentSheet(paymentSheet, currentConfig, currentClientSecret)
            }
        }
    ) {
        Text("Checkout")
    }
}

private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    paymentIntentClientSecret: String
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "FinanciallyFit CA",
            customer = customerConfig,
            allowsDelayedPaymentMethods = true
        )
    )
}

private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            println("Payment canceled")
        }
        is PaymentSheetResult.Failed -> {
            println("Payment failed: ${paymentSheetResult.error}")
        }
        is PaymentSheetResult.Completed -> {
            println("Payment completed successfully")
        }
    }
}
