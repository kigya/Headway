package dev.kigya.headway.core.session.android.google

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError

class HeadwayAndroidGoogleSignInFlow(
    private val activity: ComponentActivity,
) {

    private var pendingGoogleResult: ((Outcome<SessionDomainError, String>) -> Unit)? = null

    private val googleSignInLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { activityResult ->
        val callback = pendingGoogleResult
        pendingGoogleResult = null
        if (callback == null) {
            return@registerForActivityResult
        }
        val data: Intent? = activityResult.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                callback(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
            } else {
                callback(Outcome.success(idToken))
            }
        } catch (exception: ApiException) {
            when (exception.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED,
                CommonStatusCodes.CANCELED,
                -> callback(Outcome.failure(SessionDomainError.GoogleSignInCancelled))

                else -> callback(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
            }
        }
    }

    fun beginGoogleSignInForIdToken(
        onResult: (Outcome<SessionDomainError, String>) -> Unit,
    ) {
        activity.runOnUiThread {
            val webClientId = resolveGoogleWebClientId(activity)
            if (webClientId.isEmpty()) {
                onResult(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                return@runOnUiThread
            }
            pendingGoogleResult = onResult
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(activity, options)
            client.signOut().addOnCompleteListener {
                activity.runOnUiThread {
                    googleSignInLauncher.launch(
                        GoogleSignIn.getClient(activity, options).signInIntent,
                    )
                }
            }
        }
    }

    fun cancelPendingGoogleSignIn() {
        pendingGoogleResult = null
    }

    fun onDestroy() {
        val pending = pendingGoogleResult
        pendingGoogleResult = null
        pending?.let { it(Outcome.failure(SessionDomainError.GoogleSignInUnavailable)) }
    }
}

private fun resolveGoogleWebClientId(activity: ComponentActivity): String {
    val applicationInfo = activity.packageManager.getApplicationInfo(
        activity.packageName,
        PackageManager.GET_META_DATA,
    )
    return applicationInfo.metaData?.getString(HEADWAY_GOOGLE_WEB_CLIENT_ID_META_KEY)?.trim().orEmpty()
}

private const val HEADWAY_GOOGLE_WEB_CLIENT_ID_META_KEY: String = "headway.google.webClientId"
