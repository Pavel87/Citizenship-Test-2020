package com.pacmac.citizenship.canada

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.pacmac.citizenship.canada.ads.AdsRepository
import com.pacmac.citizenship.canada.ads.BillingRepository
import com.pacmac.citizenship.canada.ui.navigation.AppNavGraph
import com.pacmac.citizenship.canada.ui.theme.CitizenshipTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var adsRepository: AdsRepository
    @Inject lateinit var billingRepository: BillingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        MobileAds.initialize(applicationContext) {
            adsRepository.initInterstitial()
            adsRepository.initRewarded()
        }
        billingRepository.init()

        setContent {
            CitizenshipTheme {
                val navController = rememberNavController()
                val isAdFree by billingRepository.isAdFreeFlow.collectAsStateWithLifecycle()
                AppNavGraph(
                    navController = navController,
                    isAdFree = isAdFree,
                    onShowInterstitial = { adsRepository.showInterstitial(this) },
                    onShowRewarded = { callback ->
                        lifecycleScope.launch {
                            callback(adsRepository.showRewarded(this@MainActivity))
                        }
                    },
                    onRemoveAds = { lifecycleScope.launch { billingRepository.launchPurchaseFlow(this@MainActivity) } }
                )
            }
        }
    }
}
