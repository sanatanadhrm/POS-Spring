package com.example.cashier.infrastructures.export.pdf;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.example.cashier.applications.dto.notification.receipt.ReceiptData;
import com.example.cashier.applications.dto.notification.receipt.ReceiptItemData;
import com.example.cashier.applications.port.output.ReceiptGeneratorPort;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OpenPdfReceiptGenerator implements ReceiptGeneratorPort {
    private static final DecimalFormat CURRENCY_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    @Override
    public byte[] generateReceipt(ReceiptData data) {
        log.info("[PDF GENERATOR] Mulai membuat struk untuk Order ID: {}", data.getOrderId());
        // 1. Tentukan ukuran kertas kasir thermal roll (Lebar: 80mm ≈ 226 pt, Tinggi
        // dinamis: 600 pt)
        Rectangle pageSize = new Rectangle(226, 600);
        Document document = new Document(pageSize, 10, 10, 15, 15); // Margin tipis (kiri, kanan, atas, bawah)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            // Font yang umum untuk struk kasir (Courier/Monospace atau Helvetica)
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 7);
            // ==========================================
            // A. HEADER TOKO
            // ==========================================
            Paragraph title = new Paragraph(data.getStoreName().toUpperCase(), fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            Paragraph storeAddress = new Paragraph(
                    data.getStoreAddress() != null ? data.getStoreAddress() : "Jl. Kemitraan No. 12, Jakarta",
                    fontSmall);
            storeAddress.setAlignment(Element.ALIGN_CENTER);
            document.add(storeAddress);
            document.add(new Paragraph("--------------------------------------------------", fontRegular));
            // ==========================================
            // B. METADATA TRANSAKSI (Order, Kasir, Tanggal)
            // ==========================================
            PdfPTable metaTable = new PdfPTable(2);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[] { 1, 1 });
            addCellNoBorder(metaTable, "No: #" + shortenId(data.getOrderId()), fontRegular, Element.ALIGN_LEFT);
            addCellNoBorder(metaTable, data.getTransactionDate(), fontRegular, Element.ALIGN_RIGHT);
            addCellNoBorder(metaTable, "Kasir: " + data.getCashierName(), fontRegular, Element.ALIGN_LEFT);
            addCellNoBorder(metaTable,
                    data.getOrderType() + (data.getTableNumber() != null ? " (" + data.getTableNumber() + ")" : ""),
                    fontRegular, Element.ALIGN_RIGHT);
            document.add(metaTable);
            document.add(new Paragraph("--------------------------------------------------", fontRegular));
            // ==========================================
            // C. TABEL ITEM BELANJA
            // ==========================================
            PdfPTable itemTable = new PdfPTable(2);
            itemTable.setWidthPercentage(100);
            itemTable.setWidths(new float[] { 2.5f, 1.5f }); // Kolom Nama Item (lebar), Kolom Subtotal (kanan)
            if (data.getItems() != null && !data.getItems().isEmpty()) {
                for (ReceiptItemData item : data.getItems()) {
                    // Baris 1: Nama Produk
                    addCellNoBorder(itemTable, item.getProductName(), fontBold, Element.ALIGN_LEFT);
                    addCellNoBorder(itemTable, "Rp " + CURRENCY_FORMAT.format(item.getSubtotal()), fontBold,
                            Element.ALIGN_RIGHT);
                    // Baris 2: Qty x Harga Satuan
                    String detailQty = "  " + item.getQty() + " x Rp " + CURRENCY_FORMAT.format(item.getUnitPrice());
                    addCellNoBorder(itemTable, detailQty, fontSmall, Element.ALIGN_LEFT);
                    addCellNoBorder(itemTable, "", fontSmall, Element.ALIGN_RIGHT); // cell kosong untuk spasi
                }
            } else {
                addCellNoBorder(itemTable, "1x Pesanan Kasir", fontBold, Element.ALIGN_LEFT);
                addCellNoBorder(itemTable, "Rp " + CURRENCY_FORMAT.format(data.getTotalPrice()), fontBold,
                        Element.ALIGN_RIGHT);
            }
            document.add(itemTable);
            document.add(new Paragraph("==================================", fontRegular));
            // ==========================================
            // D. TOTAL & PEMBAYARAN
            // ==========================================
            PdfPTable paymentTable = new PdfPTable(2);
            paymentTable.setWidthPercentage(100);
            paymentTable.setWidths(new float[] { 1.5f, 1.5f });
            addCellNoBorder(paymentTable, "TOTAL:", fontBold, Element.ALIGN_LEFT);
            addCellNoBorder(paymentTable, formatCurrency(data.getTotalPrice()), fontBold, Element.ALIGN_RIGHT);
            addCellNoBorder(paymentTable, "Bayar (" + data.getPaymentMethod() + "):", fontRegular, Element.ALIGN_LEFT);
            addCellNoBorder(paymentTable, formatCurrency(data.getCashReceived()), fontRegular,
                    Element.ALIGN_RIGHT);
            addCellNoBorder(paymentTable, "Kembalian:", fontRegular, Element.ALIGN_LEFT);
            addCellNoBorder(paymentTable, formatCurrency(data.getChangeAmount()), fontRegular,
                    Element.ALIGN_RIGHT);
            document.add(paymentTable);
            document.add(new Paragraph("--------------------------------------------------", fontRegular));
            // ==========================================
            // E. FOOTER UCAPAN
            // ==========================================
            Paragraph footer = new Paragraph(
                    "Terima Kasih Atas Kunjungan Anda!\nBarang yang sudah dibeli tidak dapat ditukar.", fontSmall);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(5);
            document.add(footer);
            document.close();
            log.info("[PDF GENERATOR] Sukses membuat file PDF struk (Ukuran: {} bytes)", baos.size());
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("[PDF GENERATOR] Gagal membuat struk PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal men-generate PDF struk: " + e.getMessage(), e);
        }
    }

    // Helper untuk membuat cell tanpa garis border (tampilan bersih)
    private void addCellNoBorder(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPaddingBottom(2f);
        table.addCell(cell);
    }

    // Helper memotong UUID agar rapi di struk (misal: "f1aeca1b...")
    private String shortenId(String id) {
        if (id == null)
            return "-";
        return id.length() > 8 ? id.substring(0, 8) : id;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return CURRENCY_FORMAT.format(amount);
    }
}
