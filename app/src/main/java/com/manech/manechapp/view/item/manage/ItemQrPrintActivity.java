package com.manech.manechapp.view.item.manage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.widget.Toast;

import com.manech.manechapp.R;

import java.util.ArrayList;

public class ItemQrPrintActivity extends Activity {
    public static final String EXTRA_PAYLOADS = "item_qr_payloads";
    public static final String EXTRA_TITLES = "item_qr_titles";
    public static final String EXTRA_CODES = "item_qr_codes";
    public static final String EXTRA_JOB_NAME = "item_qr_job_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> payloads = getIntent().getStringArrayListExtra(EXTRA_PAYLOADS);
        ArrayList<String> titles = getIntent().getStringArrayListExtra(EXTRA_TITLES);
        ArrayList<String> codes = getIntent().getStringArrayListExtra(EXTRA_CODES);
        String jobName = getIntent().getStringExtra(EXTRA_JOB_NAME);
        if (payloads == null || titles == null || codes == null || payloads.isEmpty()
                || payloads.size() != titles.size() || payloads.size() != codes.size()) {
            finish();
            return;
        }

        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        if (printManager == null) {
            showPrintError();
            finish();
            return;
        }

        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("manech_qr", "Manech QR", 300, 300))
                .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                .build();
        try {
            printManager.print(jobName == null ? getString(R.string.item_qr_print_job) : jobName,
                    new ItemQrPrintAdapter(payloads, titles, codes,
                            jobName == null ? getString(R.string.item_qr_print_job) : jobName,
                            this::finish),
                    attributes);
        } catch (RuntimeException exception) {
            showPrintError();
            finish();
        }
    }

    private void showPrintError() {
        Toast.makeText(this, R.string.receipt_unavailable, Toast.LENGTH_LONG).show();
    }
}
