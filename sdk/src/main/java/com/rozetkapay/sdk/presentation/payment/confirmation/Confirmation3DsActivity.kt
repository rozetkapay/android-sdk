package com.rozetkapay.sdk.presentation.payment.confirmation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rozetkapay.sdk.BuildConfig
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.models.payment.ConfirmPaymentResult
import com.rozetkapay.sdk.presentation.BaseRozetkaPayActivity
import com.rozetkapay.sdk.presentation.components.LoadingScreen
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.util.Logger

internal class Confirmation3DsActivity : BaseRozetkaPayActivity() {

    private val parameters: Confirmation3DsContract.Parameters by lazy {
        Confirmation3DsContract.Parameters.fromIntent(intent)!!
    }

    @VisibleForTesting
    internal var viewModelFactory: ViewModelProvider.Factory = Confirmation3DsViewModel.Factory {
        parameters
    }

    private val viewModel: Confirmation3DsViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is Confirmation3DsEvent.Result -> {
                            finishWithResult(event.result)
                        }
                    }
                }
            }
            RozetkaPayTheme(
                themeConfigurator = parameters.themeConfigurator
            ) {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                Screen3Ds(
                    state = state,
                    onAction = { viewModel.onAction(it) },
                )
            }
        }
    }

    private fun finishWithResult(result: ConfirmPaymentResult) {
        setActivityResult(result)
        finish()
    }

    private fun setActivityResult(result: ConfirmPaymentResult) {
        setResult(
            RESULT_OK,
            Intent().putExtras(
                Confirmation3DsContract.Result(
                    confirmResult = result
                ).toBundle()
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen3Ds(
    state: Confirmation3DsState,
    onAction: (Confirmation3DsAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onAction(Confirmation3DsAction.ManuallyClosed) }
                                .padding(8.dp),
                            painter = painterResource(id = R.drawable.rozetka_pay_ic_close),
                            contentDescription = "close-button",
                            tint = DomainTheme.colors.appBarIcon
                        )
                    },
                    title = {}
                )
                if (state.showToolbarLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        strokeCap = StrokeCap.Round,
                        color = DomainTheme.colors.primary,
                        trackColor = DomainTheme.colors.surface
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            WebView(
                modifier = Modifier.matchParentSize(),
                url = state.url,
                onPageLoadingStarted = { onAction(Confirmation3DsAction.PageLoadingStarted) },
                onPageLoadingFinished = { onAction(Confirmation3DsAction.PageLoadingFinished) },
                onRequestUrlChange = { onAction(Confirmation3DsAction.UrlChange(it)) },
                onUnexpectedUrlError = { onAction(Confirmation3DsAction.UnexpectedUrlError(it)) }
            )
            if (state.showMainLoading) {
                LoadingScreen(
                    message = stringResource(id = R.string.rozetka_pay_payment_confirmation_3ds_loading)
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebView(
    modifier: Modifier,
    url: String,
    onPageLoadingStarted: () -> Unit,
    onPageLoadingFinished: () -> Unit,
    onRequestUrlChange: (String) -> Unit,
    onUnexpectedUrlError: (String) -> Unit,
) {
    val localContext = LocalContext.current
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                clearCache(true)
                clearHistory()
                settings.javaScriptEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.allowContentAccess = true
                settings.mediaPlaybackRequiresUserGesture = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(false)

                webChromeClient = object : WebChromeClient() {
                    override fun onPermissionRequest(request: PermissionRequest) {
                        request.grant(request.resources)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        onPageLoadingStarted()
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        onPageLoadingFinished()
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        Logger.d { "Override url loading ${request.url}" }
                        return when {
                            URLUtil.isNetworkUrl(request.url.toString()) -> {
                                onRequestUrlChange(request.url.toString())
                                true
                            }

                            else -> {
                                onUnexpectedUrlError(request.url.toString())
                                Logger.e { "Cancelled url ${request.url}" }
                                return false
                            }
                        }
                    }
                }
                if (0 != localContext.applicationInfo.flags && BuildConfig.DEBUG) {
                    WebView.setWebContentsDebuggingEnabled(true)
                }
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
        modifier = modifier
    )
}

@Composable
@Preview
private fun Screen3DsPreview() {
    RozetkaPayTheme {
        Screen3Ds(
            state = Confirmation3DsState(
                url = "https://rozetkapay.com/"
            ),
            onAction = {},
        )
    }
}