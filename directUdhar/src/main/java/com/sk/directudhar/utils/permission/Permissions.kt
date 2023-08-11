package com.sk.directudhar.utils.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.Serializable

class Permissions {

    companion object {
        var loggingEnabled = true


        fun disableLogging() {
            loggingEnabled = false
        }

        fun log(message: String?) {
            if (loggingEnabled) Log.d("Permissions", message!!)
        }

        fun checkPermissionNew(
            context: Context, permission: String, rationale: String?,
            handler: PermissionHandler
        ) {
            checkPermissionNew(context, arrayOf(permission), rationale, null, handler)
        }


        fun checkPermissionNew(
            context: Context, permission: String, rationaleId: Int,
            handler: PermissionHandler
        ) {
            var rationale: String? = null
            try {
                rationale = context.getString(rationaleId)
            } catch (ignored: Exception) {
            }
            checkPermissionNew(context, arrayOf(permission), rationale, null, handler)
        }


        fun checkPermissionNew(
            context: Context, permissionsSet: Array<String>, rationale: String?,
            options: Options?, handler: PermissionHandler
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                handler.onGranted()
                log("Android version < 23")
            } else {
                var allPermissionProvided = true
                for (aPermission in permissionsSet) {
                    if (context.checkSelfPermission(aPermission) != PackageManager.PERMISSION_GRANTED) {
                        allPermissionProvided = false
                        break
                    }
                }
                if (allPermissionProvided) {
                    handler.onGranted()
                    log("Permission(s) " + if (PermissionsActivity.permissionHandler == null) "already granted." else "just granted from settings.")
                    PermissionsActivity.permissionHandler = null
                } else {
                    PermissionsActivity.permissionHandler = handler
                    val permissionsList = permissionsSet
                    val intent: Intent = Intent(context, PermissionsActivity::class.java)
                        .putExtra(PermissionsActivity.EXTRA_PERMISSIONS, permissionsList)
                        .putExtra(PermissionsActivity.EXTRA_RATIONALE, rationale)
                        .putExtra(PermissionsActivity.EXTRA_OPTIONS, options)
                    if (options != null && options.createNewTask) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
        }


        fun checkPermissionNew(
            context: Context, permissions: Array<String>, rationaleId: Int,
            options: Options?, handler: PermissionHandler
        ) {
            var rationale: String? = null
            try {
                rationale = context.getString(rationaleId)
            } catch (ignored: Exception) {
            }
            checkPermissionNew(context, permissions, rationale, options, handler)
        }

        class Options : Serializable {
            var settingsText = "Settings"
            var rationaleDialogTitle = "Permissions Required"
            var settingsDialogTitle = "Permissions Required"
            var settingsDialogMessage = "Required permission(s) have been set" +
                    " not to ask again! Please provide them from settings."
            var sendBlockedToSettings = true
            var createNewTask = false
            fun setSettingsText(settingsText: String): Options {
                this.settingsText = settingsText
                return this
            }

            fun setCreateNewTask(createNewTask: Boolean): Options {
                this.createNewTask = createNewTask
                return this
            }

            fun setRationaleDialogTitle(rationaleDialogTitle: String): Options {
                this.rationaleDialogTitle = rationaleDialogTitle
                return this
            }

            fun setSettingsDialogTitle(settingsDialogTitle: String): Options {
                this.settingsDialogTitle = settingsDialogTitle
                return this
            }

            fun setSettingsDialogMessage(settingsDialogMessage: String): Options {
                this.settingsDialogMessage = settingsDialogMessage
                return this
            }

            fun sendDontAskAgainToSettings(send: Boolean): Options {
                sendBlockedToSettings = send
                return this
            }
        }

    }
}