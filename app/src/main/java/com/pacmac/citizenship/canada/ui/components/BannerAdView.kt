package com.pacmac.citizenship.canada.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdView(adUnitId: String, modifier: Modifier = Modifier, isAdFree: Boolean = false) {
    if (isAdFree) return
    val context = LocalContext.current
    val widthDp = LocalConfiguration.current.screenWidthDp
    // navigationBarsPadding keeps the ad above the system nav bar on edge-to-edge layouts
    Box(modifier = modifier.navigationBarsPadding()) {
        AndroidView(
            modifier = Modifier,
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, widthDp))
                    setAdUnitId(adUnitId)
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
