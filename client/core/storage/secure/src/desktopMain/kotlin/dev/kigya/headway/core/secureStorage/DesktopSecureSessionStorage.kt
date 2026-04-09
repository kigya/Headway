package dev.kigya.headway.core.secureStorage

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.outcomeSuspendCatchingOn
import kotlinx.coroutines.CoroutineDispatcher
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.notExists

class DesktopSecureSessionStorage(
    private val ioDispatcher: CoroutineDispatcher,
) : SecureSessionStorageContract {

    private val storagePath: Path =
        Path.of(System.getProperty("user.home"), ".headway", "session.enc")

    override suspend fun loadPayload(): Outcome<SecureSessionStorageError, String?> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            if (storagePath.notExists()) {
                null
            } else {
                Files.readString(storagePath)
            }
        }

    override suspend fun savePayload(payload: String): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            storagePath.parent.createDirectories()
            Files.writeString(
                storagePath,
                payload,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE,
            )
        }

    override suspend fun clear(): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            if (storagePath.exists()) {
                storagePath.deleteIfExists()
            }
        }
}
