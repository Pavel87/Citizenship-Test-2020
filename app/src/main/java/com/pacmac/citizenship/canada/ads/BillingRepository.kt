package com.pacmac.citizenship.canada.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val REMOVE_ADS_PRODUCT = "remove_ads"
        private const val TAG = "BillingRepository"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isAdFree = MutableStateFlow(false)
    val isAdFreeFlow: StateFlow<Boolean> = _isAdFree.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                purchases.forEach { handlePurchase(it) }
            }
        }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun init() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch { queryExistingPurchases() }
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected")
            }
        })
    }

    private suspend fun queryExistingPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val result = billingClient.queryPurchasesAsync(params)
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            _isAdFree.value = result.purchasesList.any {
                it.products.contains(REMOVE_ADS_PRODUCT) &&
                    it.purchaseState == Purchase.PurchaseState.PURCHASED
            }
        }
    }

    suspend fun launchPurchaseFlow(activity: Activity) {
        if (!billingClient.isReady) {
            Log.d(TAG, "Billing client not ready — reconnecting")
            val connected = awaitConnection()
            if (!connected) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Play Store unavailable. Try again.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(REMOVE_ADS_PRODUCT)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        val result = billingClient.queryProductDetails(params)
        Log.d(TAG, "queryProductDetails response: ${result.billingResult.responseCode} / ${result.billingResult.debugMessage}")

        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val productDetails = result.productDetailsList?.firstOrNull() ?: run {
                Log.d(TAG, "No product details found for $REMOVE_ADS_PRODUCT — is the product created in Play Console?")
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Purchase unavailable right now.", Toast.LENGTH_SHORT).show()
                }
                return
            }
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build()
            withContext(Dispatchers.Main) {
                billingClient.launchBillingFlow(activity, flowParams)
            }
        } else {
            Log.d(TAG, "Product details query failed: ${result.billingResult.debugMessage}")
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "Purchase unavailable right now.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun awaitConnection(): Boolean = suspendCancellableCoroutine { cont ->
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    scope.launch { queryExistingPurchases() }
                }
                if (cont.isActive) cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
            }
            override fun onBillingServiceDisconnected() {
                if (cont.isActive) cont.resume(false)
            }
        })
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(REMOVE_ADS_PRODUCT) &&
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        ) {
            _isAdFree.value = true
            if (!purchase.isAcknowledged) {
                scope.launch {
                    val params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    val result = billingClient.acknowledgePurchase(params)
                    Log.d(TAG, "Acknowledge result: ${result.responseCode}")
                }
            }
        }
    }
}
