package com.example.treine_me.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication

@Composable
actual fun PlatformToast(message: String) {
    LaunchedEffect(message) {
        val rootController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (rootController != null) {
            val alert = UIAlertController.alertControllerWithTitle(
                title = null,
                message = message,
                preferredStyle = UIAlertControllerStyleAlert
            )
            alert.addAction(UIAlertAction.actionWithTitle(
                title = "OK",
                style = UIAlertActionStyleDefault,
                handler = null
            ))
            rootController.presentViewController(alert, animated = true, completion = null)
        }
    }
}


