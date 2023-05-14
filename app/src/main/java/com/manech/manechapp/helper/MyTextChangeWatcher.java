package com.manech.manechapp.helper;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class MyTextChangeWatcher implements TextWatcher {

        private String previousText;
        private EditText editText;

    public MyTextChangeWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previousText = editText.getText().toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String currentText = s.toString();
            if (!previousText.equals(currentText)) {
                whenChangesOccure();
            }

        }

    public abstract void whenChangesOccure();

}
