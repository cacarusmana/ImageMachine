package com.caca.imagemachine.util;

import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

/**
 * @author caca rusmana on 19/03/22
 */
public final class Utility {

    private Utility() {
    }

    public static String getEditTextValue(TextInputLayout til) {
        return til.getEditText() == null ? "" : til.getEditText().getText().toString();
    }

    public static void viewsGone(View... view) {
        for (View v : view) {
            v.setVisibility(View.GONE);
        }
    }

    public static void viewsVisible(View... view) {
        for (View v : view) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public static void viewsInvisible(View... view) {
        for (View v : view) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    public static void setTextInputEditTextValue(TextInputLayout til, String value) {
        if (til.getEditText() != null) {
            til.getEditText().setText(value);
        }
    }
}
