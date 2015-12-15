package com.krp.android.recyclerwithrecycler.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by purushottam.kumar on 12/15/2015.
 */
public final class ActionIntentUtil {

    public static final String URL_TEL = "tel:";
    public static final String URL_MAP = "http://maps.google.com/maps?q=loc:";

    public static void callPhone(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(URL_TEL + number));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openMap(Context context, String lattitude, String longitude) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_MAP+lattitude+","+longitude));
        context.startActivity(intent);
    }

    public static void openWindowSoftKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static void closeWindowSoftKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
