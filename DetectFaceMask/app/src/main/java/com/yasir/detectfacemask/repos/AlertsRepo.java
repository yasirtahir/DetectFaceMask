package com.yasir.detectfacemask.repos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

import com.yasir.detectfacemask.R;


public class AlertsRepo {

    public static Dialog createMessageDialog(Activity activity, DialogInterface.OnClickListener dialogPositive, CharSequence message, int titleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppBaseTheme));
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(titleId)
                .setMessage(message).setCancelable(true)
                .setPositiveButton(android.R.string.yes, dialogPositive);
        return builder.create();
    }

    public static Dialog createQuitDialog(Activity activity, DialogInterface.OnClickListener dialogPositive, CharSequence message, String titleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.AppBaseTheme));
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(titleId)
                .setMessage(message).setCancelable(true)
                .setPositiveButton(android.R.string.yes, dialogPositive)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
