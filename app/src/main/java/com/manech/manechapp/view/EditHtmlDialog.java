package com.manech.manechapp.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.manech.manechapp.R;

public class EditHtmlDialog extends DialogFragment {

    private static final String ARG_HTML = "html";

    public interface OnOkClickListener {
        void onOkClick(String editedHtml);
    }

    private OnOkClickListener mListener;

    public static EditHtmlDialog newInstance(String html, OnOkClickListener listener) {
        EditHtmlDialog dialog = new EditHtmlDialog();
        Bundle args = new Bundle();
        args.putString(ARG_HTML, html);
        dialog.setArguments(args);
        dialog.setListener(listener);
        return dialog;
    }

    private void setListener(OnOkClickListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String html = getArguments().getString(ARG_HTML);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_html, null);

        EditText editText = view.findViewById(R.id.edit_text);
        editText.setText(html);
        WebView preview = view.findViewById(R.id.html_preview);
        Button togglePreview = view.findViewById(R.id.toggle_html_preview);
        togglePreview.setOnClickListener(v -> {
            boolean showPreview = preview.getVisibility() != View.VISIBLE;
            if (showPreview) {
                com.manech.manechapp.helper.HtmlDescription.render(
                        preview, editText.getText().toString());
            }
            preview.setVisibility(showPreview ? View.VISIBLE : View.GONE);
            editText.setVisibility(showPreview ? View.GONE : View.VISIBLE);
            togglePreview.setText(showPreview ? R.string.html_edit_source : R.string.html_preview);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String editedHtml = editText.getText().toString();
                        if (mListener != null) {
                            mListener.onOkClick(editedHtml);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        return builder.create();
    }
}
