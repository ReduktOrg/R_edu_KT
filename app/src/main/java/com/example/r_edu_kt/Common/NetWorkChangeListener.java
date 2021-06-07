package com.example.r_edu_kt.Common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.r_edu_kt.R;

public class NetWorkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!checkNetwork.isConnectedToInternet(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.dialog_net_check, null);
            builder.setView(layout_dialog);

            final AppCompatButton btnRetry = layout_dialog.findViewById(R.id.netcheckbtn);

            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.show();

            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
    }
}
