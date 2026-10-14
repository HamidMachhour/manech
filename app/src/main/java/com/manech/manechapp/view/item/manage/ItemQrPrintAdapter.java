package com.manech.manechapp.view.item.manage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import net.glxn.qrgen.android.QRCode;

import java.io.FileOutputStream;
import java.util.List;

public class ItemQrPrintAdapter extends PrintDocumentAdapter {
    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final int ITEMS_PER_PAGE = 6;
    private static final int QR_SIZE = 600;

    private final List<String> payloads;
    private final List<String> titles;
    private final List<String> codes;
    private final String jobName;
    private final Runnable onFinished;

    public ItemQrPrintAdapter(List<String> payloads, List<String> titles, List<String> codes,
                              String jobName, Runnable onFinished) {
        this.payloads = payloads;
        this.titles = titles;
        this.codes = codes;
        this.jobName = jobName;
        this.onFinished = onFinished;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback,
                         android.os.Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        int pageCount = pageCount();
        PrintDocumentInfo info = new PrintDocumentInfo.Builder(jobName)
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pageCount)
                .build();
        callback.onLayoutFinished(info, !newAttributes.equals(oldAttributes));
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {
        android.graphics.pdf.PdfDocument document = new android.graphics.pdf.PdfDocument();
        try {
            for (int pageIndex = 0; pageIndex < pageCount(); pageIndex++) {
                if (!isRequested(pageIndex, pages)) {
                    continue;
                }
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    return;
                }
                android.graphics.pdf.PdfDocument.Page page = document.startPage(
                        new android.graphics.pdf.PdfDocument.PageInfo.Builder(
                                PAGE_WIDTH, PAGE_HEIGHT, pageIndex + 1).create());
                drawPage(page.getCanvas(), pageIndex);
                document.finishPage(page);
            }
            try (FileOutputStream output = new FileOutputStream(destination.getFileDescriptor())) {
                document.writeTo(output);
            }
            callback.onWriteFinished(pages);
        } catch (Exception exception) {
            String message = exception.getLocalizedMessage();
            callback.onWriteFailed(message == null ? exception.getClass().getSimpleName() : message);
        } finally {
            document.close();
        }
    }

    private void drawPage(Canvas canvas, int pageIndex) throws Exception {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int firstItem = pageIndex * ITEMS_PER_PAGE;
        int cellWidth = PAGE_WIDTH / 2;
        int cellHeight = PAGE_HEIGHT / 3;
        int qrSize = 190;
        int qrTopOffset = 20;
        int labelTopOffset = qrTopOffset + qrSize + 20;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(14);

        for (int index = 0; index < ITEMS_PER_PAGE; index++) {
            int itemIndex = firstItem + index;
            if (itemIndex >= payloads.size()) {
                break;
            }
            int column = index % 2;
            int row = index / 2;
            int centerX = column * cellWidth + cellWidth / 2;
            int top = row * cellHeight;
            Bitmap qrBitmap = QRCode.from(payloads.get(itemIndex))
                    .withSize(QR_SIZE, QR_SIZE)
                    .bitmap();
            canvas.drawBitmap(qrBitmap, null, new Rect(
                    centerX - qrSize / 2,
                    top + qrTopOffset,
                    centerX + qrSize / 2,
                    top + qrTopOffset + qrSize), paint);
            String title = titles.get(itemIndex);
            canvas.drawText(title, centerX, top + labelTopOffset, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            paint.setTextSize(11);
            canvas.drawText(codes.get(itemIndex),
                    centerX, top + labelTopOffset + 18, paint);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextSize(14);
            qrBitmap.recycle();
        }
    }

    private int pageCount() {
        return (payloads.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
    }

    @Override
    public void onFinish() {
        if (onFinished != null) {
            onFinished.run();
        }
    }

    private boolean isRequested(int pageIndex, PageRange[] pages) {
        if (pages == null || pages.length == 0) {
            return false;
        }
        for (PageRange range : pages) {
            if (pageIndex >= range.getStart() && pageIndex <= range.getEnd()) {
                return true;
            }
        }
        return false;
    }
}
