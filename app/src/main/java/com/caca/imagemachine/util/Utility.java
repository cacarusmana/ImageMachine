package com.caca.imagemachine.util;

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
}
