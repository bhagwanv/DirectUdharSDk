package com.sk.directudhar.utils.permission

import android.content.Context
import android.widget.Toast

abstract class PermissionHandler {
    abstract fun onGranted()


    open fun onDenied(context: Context?, deniedPermissions: ArrayList<String>) {
        if (Permissions.loggingEnabled) {
            val builder = StringBuilder()
            builder.append("Denied:")
            for (permission in deniedPermissions) {
                builder.append(" ")
                builder.append(permission)
            }
            Permissions.log(builder.toString())
        }
        Toast.makeText(context, "Permission Denied.", Toast.LENGTH_SHORT).show()
    }


    open fun onBlocked(context: Context?, blockedList: ArrayList<String?>): Boolean {
        if (Permissions.loggingEnabled) {
            val builder = StringBuilder()
            builder.append("Set not to ask again:")
            for (permission in blockedList) {
                builder.append(" ")
                builder.append(permission)
            }
            Permissions.log(builder.toString())
        }
        return false
    }

    open fun onJustBlocked(
        context: Context?, justBlockedList: ArrayList<String?>,
        deniedPermissions: ArrayList<String>
    ) {
        if (Permissions.loggingEnabled) {
            val builder = StringBuilder()
            builder.append("Just set not to ask again:")
            for (permission in justBlockedList) {
                builder.append(" ")
                builder.append(permission)
            }
            Permissions.log(builder.toString())
        }
        onDenied(context, deniedPermissions)
    }
}