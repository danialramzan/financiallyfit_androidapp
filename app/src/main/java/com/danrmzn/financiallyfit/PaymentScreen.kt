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
fun PaymentScreen(finalTaskList: TaskList? = null, triggerPayment: Boolean
                  , onPaymentComplete: (Boolean) -> Unit // not used yet!
    ) {
    val paymentSheet = rememberPaymentSheet(::onPaymentSheetResult)
    val context = LocalContext.current
    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(triggerPayment) {
        if (triggerPayment) {
            user?.getIdToken(true)?.addOnSuccessListener { result ->
                val token = result.token

                // structure json text
                val requestBody = """
                {
                    "amount": ${finalTaskList?.moneyAmount?.times(100)},
                    "currency": "${finalTaskList?.currency?.lowercase()}"
                }
            """.trimIndent()

                Log.wtf("GNX", requestBody)
                // backend server url
                "https://us-central1-financiallyfit-52b32.cloudfunctions.net/payment_sheet"
                    .httpPost()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .responseJson { _, _, result ->
                        if (result is Result.Success) {
                            val responseJson = result.get().obj()
                            paymentIntentClientSecret = responseJson.getString("paymentIntent")
                            customerConfig = PaymentSheet.CustomerConfiguration(
                                id = responseJson.getString("customer"),
                                ephemeralKeySecret = responseJson.getString("ephemeralKey")
                            )
                            val publishableKey = responseJson.getString("publishableKey")
                            PaymentConfiguration.init(context, publishableKey)

                            if (customerConfig != null && paymentIntentClientSecret != null) {
                                presentPaymentSheet(paymentSheet, customerConfig!!, paymentIntentClientSecret!!)
                            }
                        } else {
                            Log.wtf("GNX", "Payment Request Failed!")
                        }
                    }
            }
        }
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
