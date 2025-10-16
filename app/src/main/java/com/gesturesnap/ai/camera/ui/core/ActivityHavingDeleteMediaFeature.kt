package com.gesturesnap.ai.camera.ui.core

import android.Manifest
import android.app.RecoverableSecurityException
import android.os.Build
import android.os.Bundle
import android.os.ConditionVariable
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.gesturesnap.ai.camera.R
import com.gesturesnap.ai.camera.extension.showToast
import com.gesturesnap.ai.camera.helper.MediaHelper
import com.gesturesnap.ai.camera.helper.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class ActivityHavingDeleteMediaFeature : BaseActivity() {

    private val condVarWaitState = ConditionVariable()

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private lateinit var requestExternalPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var onSuccessfulDelete: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentSenderLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    this.onSuccessfulDelete.invoke()
                }
            }

        requestExternalPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                condVarWaitState.open()
            }
        }
    }

    fun deleteMedia(mediaPath: String, onSuccessfulDelete: () -> Unit) {
        val uri = MediaHelper.getUriFromPath(this, mediaPath)

        if (uri == null) {
            showToast(getString(R.string.delete_media_failed))
            return
        }

        this.onSuccessfulDelete = onSuccessfulDelete

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intentSender =
                MediaStore.createDeleteRequest(contentResolver, listOf(uri)).intentSender
            intentSender.let { sender ->
                intentSenderLauncher.launch(IntentSenderRequest.Builder(sender).build())
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {
                contentResolver.delete(uri, null, null)
                MediaHelper.scanFile(this, mediaPath)
                onSuccessfulDelete.invoke()
            } catch (e: SecurityException) {
                val recoverableSecurityException = e as? RecoverableSecurityException
                val intentSender =
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender

                intentSender?.let { sender ->
                    intentSenderLauncher.launch(IntentSenderRequest.Builder(sender).build())
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                var hasWriteExternalPermission = true

                if (!PermissionHelper.isWriteExternalStoragePermissionGranted(this@ActivityHavingDeleteMediaFeature)) {
                    requestExternalPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    condVarWaitState.close()
                    hasWriteExternalPermission =
                        condVarWaitState.block(1000) // stop and wait until the permission is granted
                }

                if (hasWriteExternalPermission) {
                    contentResolver.delete(uri, null, null)
                    MediaHelper.scanFile(this@ActivityHavingDeleteMediaFeature, mediaPath)
                    withContext(Dispatchers.Main) {
                        onSuccessfulDelete.invoke()
                    }
                }
            }
        }
    }
}