package dev.kigya.headway.admin.internal.core.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Serializable
internal data class AdminSession(
    @SerialName("githubLogin")
    val githubLogin: String,
    @SerialName("githubAvatarUrl")
    val githubAvatarUrl: String? = null,
    @SerialName("hasRepositoryAccess")
    val hasRepositoryAccess: Boolean,
    @SerialName("createdAt")
    val createdAt: Long,
) {
    companion object {
        private val json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
        private val encoder = Base64.getUrlEncoder().withoutPadding()
        private val decoder = Base64.getUrlDecoder()

        fun sign(
            session: AdminSession,
            secret: String,
        ): String {
            val payload = json.encodeToString(serializer(), session)
            val payloadBytes = payload.encodeToByteArray()
            val encodedPayload = encoder.encodeToString(payloadBytes)
            val signature = signature(payloadBytes, secret)
            return "$encodedPayload.$signature"
        }

        fun verify(
            cookieValue: String,
            secret: String,
            ttlMs: Long,
            nowMs: Long = System.currentTimeMillis(),
        ): AdminSession? = parseSignedCookie(cookieValue)
            ?.takeIf { it.hasValidSignature(secret) }
            ?.decodeSession()
            ?.takeIf { it.isActive(nowMs = nowMs, ttlMs = ttlMs) }

        private fun signature(
            payloadBytes: ByteArray,
            secret: String,
        ): String {
            val mac = Mac.getInstance(HMAC_SHA256).apply {
                init(SecretKeySpec(secret.encodeToByteArray(), HMAC_SHA256))
            }
            return encoder.encodeToString(mac.doFinal(payloadBytes))
        }

        private fun parseSignedCookie(cookieValue: String): SignedCookiePayload? {
            val parts = cookieValue.split('.', limit = 2)
            if (parts.size != SIGNED_COOKIE_PARTS_COUNT) {
                return null
            }

            val payloadBytes = runCatching { decoder.decode(parts.first()) }.getOrNull() ?: return null
            val signatureBytes = runCatching { decoder.decode(parts.last()) }.getOrNull() ?: return null
            return SignedCookiePayload(
                payloadBytes = payloadBytes,
                signatureBytes = signatureBytes,
            )
        }

        private fun SignedCookiePayload.hasValidSignature(secret: String): Boolean {
            val expectedSignature = runCatching {
                decoder.decode(signature(payloadBytes, secret))
            }.getOrNull() ?: return false
            return MessageDigest.isEqual(signatureBytes, expectedSignature)
        }

        private fun SignedCookiePayload.decodeSession(): AdminSession? = runCatching {
            json.decodeFromString(serializer(), payloadBytes.decodeToString())
        }.getOrNull()

        private fun AdminSession.isActive(
            nowMs: Long,
            ttlMs: Long,
        ): Boolean = hasRepositoryAccess &&
            createdAt <= nowMs &&
            nowMs - createdAt <= ttlMs
    }
}

private data class SignedCookiePayload(
    val payloadBytes: ByteArray,
    val signatureBytes: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignedCookiePayload

        if (!payloadBytes.contentEquals(other.payloadBytes)) return false
        if (!signatureBytes.contentEquals(other.signatureBytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = payloadBytes.contentHashCode()
        result = 31 * result + signatureBytes.contentHashCode()
        return result
    }
}

private const val HMAC_SHA256 = "HmacSHA256"
private const val SIGNED_COOKIE_PARTS_COUNT = 2
