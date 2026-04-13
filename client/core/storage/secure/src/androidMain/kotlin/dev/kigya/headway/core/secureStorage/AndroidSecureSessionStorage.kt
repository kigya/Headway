package dev.kigya.headway.core.secureStorage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.outcomeSuspendCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AndroidSecureSessionStorage(
    private val ioDispatcher: CoroutineDispatcher,
    context: Context,
    dataStoreScope: CoroutineScope,
) : SecureSessionStorageContract {

    private val applicationContext = context.applicationContext
    private val cipher = AndroidSessionPayloadCipher()
    private val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            scope = dataStoreScope,
            produceFile = { applicationContext.preferencesDataStoreFile(DATASTORE_FILE_NAME) },
        )

    override suspend fun loadPayload(): Outcome<SecureSessionStorageError, String?> =
        outcomeSuspendCatching(
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            withContext(ioDispatcher) {
                val wrapped = dataStore.data.first()[payloadKey] ?: return@withContext null
                cipher.decryptFromBase64(wrapped)
            }
        }

    override suspend fun savePayload(payload: String): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatching(
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            withContext(ioDispatcher) {
                val wrapped = cipher.encryptToBase64(payload)
                dataStore.edit { preferences ->
                    preferences[payloadKey] = wrapped
                }
            }
        }

    @Suppress("FunctionSignature")
    override suspend fun clear(): Outcome<SecureSessionStorageError, Unit> = outcomeSuspendCatching(
        mapError = { SecureSessionStorageError.OperationFailed(it) },
    ) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences.remove(payloadKey)
            }
        }
    }
}

private const val DATASTORE_FILE_NAME: String = "headway_secure_session"
private val payloadKey = stringPreferencesKey("session_payload_enc")
