package com.appWeb.cotizacion.pdf;

import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import com.lowagie.text.*;
import com.lowagie.text.Font;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.*;

import java.time.format.DateTimeFormatter;

@Component
public class PdfStylesUtil {

    private static final String FUENTE = FontFactory.HELVETICA;

    public void agregarTitulo(Document doc, String titulo) throws DocumentException {
        Font font = FontFactory.getFont(FUENTE, 20, Font.BOLD, new Color(14, 12, 40));
        Paragraph title = new Paragraph(titulo, font);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        doc.add(title);
    }

    public void agregarInformacionGeneral(Document doc, Cotizacion c, String usuario) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
        Font label = FontFactory.getFont(FUENTE, 10, Font.BOLD);
        Font value = FontFactory.getFont(FUENTE, 10);

        table.addCell(celda("Número:", label, true));
        table.addCell(celda(c.getNumeroCotizacion(), value, false));
        table.addCell(celda("Fecha:", label, true));
        table.addCell(celda(c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), value, false));
        // ... puedes agregar más campos aquí como nombre del cliente o vehículo

        doc.add(table);
    }

    public void agregarTablaProductos(Document doc, Cotizacion c) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f, 1f, 1.2f, 1.2f});

        Font head = FontFactory.getFont(FUENTE, 10, Font.BOLD, Color.WHITE);
        Color headBg = Color.BLACK;
        String[] headers = {"Producto", "Imagen", "Cant.", "Precio", "Subtotal"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, head));
            cell.setBackgroundColor(headBg);
            cell.setPadding(8);
            table.addCell(cell);
        }

        Font content = FontFactory.getFont(FUENTE, 9);
        for (DetalleCotizacion d : c.getDetalles()) {
            table.addCell(new PdfPCell(new Phrase(d.getProducto().getName(), content)));
            try {
                Image img = Image.getInstance(d.getProducto().getImageUrl());
                img.scaleToFit(50, 50);
                PdfPCell imageCell = new PdfPCell(img, true);
                table.addCell(imageCell);
            } catch (Exception e) {
                table.addCell(new PdfPCell(new Phrase("Imagen no disponible")));
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

        Font bold = FontFactory.getFont(FUENTE, 10, Font.BOLD);
        Font reg = FontFactory.getFont(FUENTE, 10);

        table.addCell(celda("Subtotal:", bold, false));
        table.addCell(celda(String.format("S/ %.2f", c.getSubtotal()), reg, true));
        table.addCell(celda("IGV (18%): ",bold, false));
        table.addCell(celda("S/."+ c.getIgv(), reg, true));
        table.addCell(celda("TOTAL:", bold, false));
        table.addCell(celda("S/ " + c.getTotal(), bold, true));

        doc.add(table);
    }

    public void agregarTerminos(Document doc) throws DocumentException {
        Font font = FontFactory.getFont(FUENTE, 9);
        doc.add(new Paragraph("TÉRMINOS Y CONDICIONES", FontFactory.getFont(FUENTE, 12, Font.BOLD)));
        doc.add(new Paragraph("• Esta cotización es válida por 30 días.", font));
        doc.add(new Paragraph("• Sujeta a disponibilidad de productos.", font));
        // ... etc
    }

    public void agregarFirma(Document doc, Cotizacion c, String usuario) throws DocumentException {
        PdfPTable firma = new PdfPTable(2);
        firma.setWidthPercentage(100);
        firma.setSpacingBefore(50);

        Font font = FontFactory.getFont(FUENTE, 10);
        firma.addCell(firmaCell("ELABORADO POR", usuario, font));
        firma.addCell(firmaCell("ACEPTADO POR", c.getCliente().getFirstName() + " " + c.getCliente().getLastName(), font));

        doc.add(firma);
    }

    private PdfPCell celda(String text, Font font, boolean fondoRojo) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        if (fondoRojo) cell.setBackgroundColor(new Color(254, 0, 28));
        return cell;
    }

    private PdfPCell firmaCell(String label, String name, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        Paragraph p = new Paragraph();
        p.add(new Chunk("____________________\n", font));
        p.add(new Chunk(label + "\n", font));
        p.add(new Chunk(name, font));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        return cell;
    }
}