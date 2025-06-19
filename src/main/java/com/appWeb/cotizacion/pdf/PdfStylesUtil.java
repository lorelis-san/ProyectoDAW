package com.appWeb.cotizacion.pdf;

import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import com.lowagie.text.*;
import com.lowagie.text.Font;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Component;

import java.awt.*;

import java.time.format.DateTimeFormatter;

@Component
public class PdfStylesUtil {

    private static final String FUENTE = FontFactory.HELVETICA;
    private static final Color COLOR_BASE = new Color(31, 25, 60); // #1F193C
    private static final Color COLOR_TEXTO_CLARO = Color.WHITE;
    private static final Color COLOR_LINEA = new Color(200, 200, 200); // Gris claro para separación

    public void agregarTitulo(Document doc, String titulo) throws DocumentException {
        Font font = FontFactory.getFont(FUENTE, 22, Font.BOLD, COLOR_BASE);
        Paragraph title = new Paragraph(titulo.toUpperCase(), font);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(25);
        doc.add(title);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(COLOR_LINEA);
        doc.add(new Chunk(separator));
    }

    public void agregarInformacionGeneral(Document doc, Cotizacion c, String usuario) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{2f, 5f});
        Font label = FontFactory.getFont(FUENTE, 10, Font.BOLD, COLOR_BASE);
        Font value = FontFactory.getFont(FUENTE, 10);

        addRow(table, "N° Cotización:", c.getNumeroCotizacion(), label, value);
        addRow(table, "Fecha:", c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), label, value);
        String tipoDoc = c.getCliente().getTypeDocument();
        String nombreCliente = tipoDoc.equalsIgnoreCase("RUC")
                ? c.getCliente().getBusinessName()
                : c.getCliente().getFirstName() + " " + c.getCliente().getLastName();
        addRow(table, "Tipo Documento:", tipoDoc, label, value);
        addRow(table, "N° Documento:", c.getCliente().getDocumentNumber(), label, value);
        addRow(table, "Cliente:", nombreCliente, label, value);
        addRow(table, "Email:", c.getCliente().getEmail(), label, value);
        addRow(table, "Celular:", c.getCliente().getPhoneNumber(), label, value);
        addRow(table, "Placa:", c.getVehiculo().getPlaca(), label, value);
        addRow(table, "Marca:", c.getVehiculo().getMarca(), label, value);
        addRow(table, "Modelo:", c.getVehiculo().getModelo(), label, value);

        if (c.getObservaciones() != null && !c.getObservaciones().isEmpty()) {
            addRow(table, "Observaciones:", c.getObservaciones(), label, value);
        }

        doc.add(table);
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(4);
        cell2.setPadding(4);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    public void agregarTablaProductos(Document doc, Cotizacion c) throws DocumentException {
        Paragraph title = new Paragraph("DETALLE DE PRODUCTOS", FontFactory.getFont(FUENTE, 12, Font.BOLD, COLOR_BASE));
        title.setSpacingBefore(15);
        title.setSpacingAfter(10);
        doc.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 1f, 1.5f, 1.5f});
        Font head = FontFactory.getFont(FUENTE, 10, Font.BOLD, COLOR_TEXTO_CLARO);

        String[] headers = {"Producto", "Imagen", "Cantidad", "Precio", "Subtotal"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, head));
            cell.setBackgroundColor(COLOR_BASE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        Font content = FontFactory.getFont(FUENTE, 9);
        for (DetalleCotizacion d : c.getDetalles()) {
            table.addCell(new PdfPCell(new Phrase(d.getProducto().getName(), content)));

            try {
                Image img = Image.getInstance(d.getProducto().getImageUrl());
                img.scaleToFit(40, 40);
                PdfPCell imgCell = new PdfPCell(img, true);
                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                imgCell.setPadding(4);
                table.addCell(imgCell);
            } catch (Exception e) {
                table.addCell(new PdfPCell(new Phrase("Imagen no disponible", content)));
            }

            table.addCell(new PdfPCell(new Phrase(d.getCantidad().toString(), content)));
            table.addCell(new PdfPCell(new Phrase("S/ " + d.getPrecioUnitario(), content)));
            table.addCell(new PdfPCell(new Phrase("S/ " + d.getSubtotal(), content)));
        }

        doc.add(table);
    }

    public void agregarTotales(Document doc, Cotizacion c) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Font label = FontFactory.getFont(FUENTE, 10, Font.BOLD);
        Font value = FontFactory.getFont(FUENTE, 10);

        addRow(table, "Subtotal:", String.format("S/ %.2f", c.getSubtotal()), label, value);
        addRow(table, "IGV (18%):", String.format("S/ %.2f", c.getIgv()), label, value);
        addRow(table, "TOTAL:", String.format("S/ %.2f", c.getTotal()), label, value);

        doc.add(table);
    }

    public void agregarTerminos(Document doc) throws DocumentException {
        Paragraph title = new Paragraph("TÉRMINOS Y CONDICIONES", FontFactory.getFont(FUENTE, 12, Font.BOLD, COLOR_BASE));
        title.setSpacingBefore(20);
        doc.add(title);

        Font font = FontFactory.getFont(FUENTE, 9);
        doc.add(new Paragraph("• Esta cotización es válida por 30 días.", font));
        doc.add(new Paragraph("• Sujeta a disponibilidad de productos.", font));
        doc.add(new Paragraph("• Precios sujetos a cambios sin previo aviso.", font));
    }

    public void agregarFirma(Document doc, Cotizacion c, String usuario) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(70);
        Font font = FontFactory.getFont(FUENTE, 10);


        String tipoDoc = c.getCliente().getTypeDocument();
        String nombreCliente = tipoDoc.equalsIgnoreCase("RUC")
                ? c.getCliente().getBusinessName()
                : c.getCliente().getFirstName() + " " + c.getCliente().getLastName();



        table.addCell(firmaCell("ELABORADO POR", usuario, font));
        table.addCell(firmaCell("ACEPTADO POR", nombreCliente, font));
        doc.add(table);
    }

    private PdfPCell firmaCell(String label, String name, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(new Chunk("____________________\n", font));
        p.add(new Chunk(label + "\n", font));
        p.add(new Chunk(name, font));
        cell.addElement(p);
        return cell;
    }
}
