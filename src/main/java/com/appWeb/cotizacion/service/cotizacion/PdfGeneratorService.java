package com.appWeb.cotizacion.service.cotizacion;

import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    private static final String FUENTE = FontFactory.HELVETICA;

    private static class BackgroundPageEvent extends PdfPageEventHelper {
        private final Image background;

        public BackgroundPageEvent(Image background) {
            this.background = background;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte canvas = writer.getDirectContentUnder();
                background.setAbsolutePosition(0, 0);
                background.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                canvas.addImage(background);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ByteArrayInputStream generarCotizacionPDF(Cotizacion cotizacion) {
        Document document = new Document(PageSize.A4, 60, 60, 170, 130); // márgenes corregidos
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String nombreUsuario = cotizacion.getUser().getNombre() + " " + cotizacion.getUser().getApellido();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            Image background = Image.getInstance("src/main/resources/static/img/plantilla.jpg");
            writer.setPageEvent(new BackgroundPageEvent(background));

            document.open();

            agregarTitulo(document);
            agregarInformacionGeneral(document, cotizacion, nombreUsuario);
            agregarTablaProductos(document, cotizacion);
            agregarTotales(document, cotizacion);
            agregarTerminosYCondiciones(document);
            agregarSeccionFirma(document, cotizacion, nombreUsuario);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void agregarTitulo(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FUENTE, 20, Font.BOLD, new Color(14, 12, 40));
        Paragraph title = new Paragraph("", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void agregarInformacionGeneral(Document document, Cotizacion cotizacion, String nombreUsuario) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
        infoTable.setSpacingAfter(15);

        Font labelFont = FontFactory.getFont(FUENTE, 10, Font.BOLD);
        Font valueFont = FontFactory.getFont(FUENTE, 10);

        infoTable.addCell(celdaInfo("Número:", labelFont, true));
        infoTable.addCell(celdaInfo(cotizacion.getNumeroCotizacion(), valueFont, false));
        infoTable.addCell(celdaInfo("Fecha:", labelFont, true));
        infoTable.addCell(celdaInfo(cotizacion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), valueFont, false));

        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
//        Color sectionBgColor = new Color(31, 28, 79);
//
//        PdfPCell clienteTitle = new PdfPCell(new Phrase("DATOS DE CLIENTE", sectionFont));
//        clienteTitle.setColspan(4);
//        clienteTitle.setBackgroundColor(sectionBgColor);
//        clienteTitle.setHorizontalAlignment(Element.ALIGN_LEFT);
//        clienteTitle.setPadding(8f);
//        infoTable.addCell(clienteTitle);

        String tipoDoc = cotizacion.getCliente().getTypeDocument().toUpperCase();
        infoTable.addCell(celdaInfo("Tipo Documento:", labelFont, true));
        infoTable.addCell(celdaInfo(tipoDoc, valueFont, false));

//        infoTable.addCell(celdaInfo("Correo:", labelFont, true));
//        infoTable.addCell(celdaInfo(cotizacion.getCliente().getEmail(), valueFont, false));
//        infoTable.addCell(celdaInfo("Celular:", labelFont, true));
//        infoTable.addCell(celdaInfo(cotizacion.getCliente().getPhoneNumber(), valueFont, false));

        infoTable.addCell(celdaInfo("Placa del Vehículo:", labelFont, true));
        infoTable.addCell(celdaInfo(cotizacion.getVehiculo().getPlaca().toUpperCase(), valueFont, false));
        infoTable.addCell(celdaInfo("Nro Documento:", labelFont, true));
        infoTable.addCell(celdaInfo(cotizacion.getCliente().getDocumentNumber(), valueFont, false));

        infoTable.addCell(celdaInfo("Marca:", labelFont, true));
        infoTable.addCell(celdaInfo(cotizacion.getVehiculo().getMarca(), valueFont, false));

//        infoTable.addCell(celdaInfo("Modelo:", labelFont, true));
//        PdfPCell modeloCell = celdaInfo(cotizacion.getVehiculo().getModelo(), valueFont, false);
//        modeloCell.setColspan(3);
//        infoTable.addCell(modeloCell);
        String nombreCliente = tipoDoc.equalsIgnoreCase("RUC")
                ? cotizacion.getCliente().getBusinessName()
                : cotizacion.getCliente().getFirstName() + " " + cotizacion.getCliente().getLastName();
        infoTable.addCell(celdaInfo("Cliente:", labelFont, true));
        infoTable.addCell(celdaInfo(nombreCliente, valueFont, false));
        if (cotizacion.getObservaciones() != null && !cotizacion.getObservaciones().trim().isEmpty()) {
            infoTable.addCell(celdaInfo("Observaciones:", labelFont, true));
            PdfPCell obsCell = celdaInfo(cotizacion.getObservaciones(), valueFont, false);
            obsCell.setColspan(3);
            infoTable.addCell(obsCell);
        }

        document.add(infoTable);
    }

    private PdfPCell celdaInfo(String texto, Font font, boolean esLabel) {
        Font finalFont = font;
        if (esLabel) {
            finalFont = FontFactory.getFont(FUENTE, 10, Font.BOLD, Color.WHITE); // texto blanco
        }
        PdfPCell cell = new PdfPCell(new Phrase(texto, finalFont));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        if (esLabel) cell.setBackgroundColor(new Color(254, 0, 28)); // #FE001C
        return cell;
    }

    private void agregarTablaProductos(Document document, Cotizacion cotizacion) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 1.5f, 1f, 1.2f, 1.2f});
        table.setSpacingAfter(15);

        Font headFont = FontFactory.getFont(FUENTE, 10, Font.BOLD, Color.WHITE);
        Color headerColor = Color.BLACK;

        String[] headers = {"Producto", "Imagen", "Cant.", "Precio Unit.", "Subtotal"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setBackgroundColor(headerColor);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        Font contentFont = FontFactory.getFont(FUENTE, 9);
        int i = 0;
        for (DetalleCotizacion d : cotizacion.getDetalles()) {
            Color bgColor = (i++ % 2 == 0) ? Color.WHITE : new Color(233, 231, 232); // #E9E7E8

            table.addCell(celdaTabla(d.getProducto().getName(), contentFont, bgColor));
            table.addCell(celdaImagen(d.getProducto().getImageUrl(), bgColor));
            table.addCell(celdaTabla(String.valueOf(d.getCantidad()), contentFont, bgColor, Element.ALIGN_CENTER));
            table.addCell(celdaTabla(String.format("S/ %.2f", d.getPrecioUnitario()), contentFont, bgColor, Element.ALIGN_RIGHT));
            table.addCell(celdaTabla(String.format("S/ %.2f", d.getSubtotal()), contentFont, bgColor, Element.ALIGN_RIGHT));
        }

        document.add(table);
    }

    private PdfPCell celdaTabla(String text, Font font, Color bgColor) {
        return celdaTabla(text, font, bgColor, Element.ALIGN_LEFT);
    }

    private PdfPCell celdaTabla(String text, Font font, Color bgColor, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell celdaImagen(String url, Color bgColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setMinimumHeight(90);

        try {
            if (url != null && !url.isEmpty()) {
                Image img = Image.getInstance(new URL(url));
                img.scaleToFit(80, 80);
                cell.addElement(img);
            } else {
                cell.addElement(new Paragraph("Sin imagen", FontFactory.getFont(FUENTE, 8, Color.GRAY)));
            }
        } catch (Exception e) {
            cell.addElement(new Paragraph("Sin imagen", FontFactory.getFont(FUENTE, 8, Color.GRAY)));
        }
        return cell;
    }

    private void agregarTotales(Document document, Cotizacion cotizacion) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setWidths(new float[]{1.5f, 1f});
        table.setSpacingAfter(20);

        Font bold = FontFactory.getFont(FUENTE, 10, Font.BOLD);
        Font regular = FontFactory.getFont(FUENTE, 10);

        table.addCell(celdaTotal("Subtotal:", bold));
        table.addCell(celdaTotal(String.format("S/ %.2f", cotizacion.getSubtotal()), regular, true));

        PdfPCell totalLabel = celdaTotal("TOTAL:", FontFactory.getFont(FUENTE, 11, Font.BOLD));
        PdfPCell totalValue = celdaTotal(String.format("S/ %.2f", cotizacion.getTotal()), FontFactory.getFont(FUENTE, 11, Font.BOLD), true);
        totalLabel.setBackgroundColor(new Color(240, 240, 240));
        totalValue.setBackgroundColor(new Color(240, 240, 240));

        table.addCell(totalLabel);
        table.addCell(totalValue);

        document.add(table);
    }

    private PdfPCell celdaTotal(String text, Font font) {
        return celdaTotal(text, font, false);
    }

    private PdfPCell celdaTotal(String text, Font font, boolean alignRight) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        if (alignRight) cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    private void agregarTerminosYCondiciones(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FUENTE, 12, Font.BOLD);
        Paragraph title = new Paragraph("TÉRMINOS Y CONDICIONES", titleFont);
        title.setSpacingBefore(10);
        title.setSpacingAfter(10);
        document.add(title);

        Font termFont = FontFactory.getFont(FUENTE, 9);
        String[] terminos = {
                "• Esta cotización tiene una validez de 30 días calendario desde la fecha de emisión.",
                "• Los precios están expresados en Soles (S/) e incluyen IGV.",
                "• El tiempo de entrega será coordinado una vez confirmado el pedido.",
                "• Para confirmar el pedido se requiere el 50% del total como adelanto.",
                "• Los productos están sujetos a disponibilidad de stock.",
                "• La empresa se reserva el derecho de modificar precios sin previo aviso."
        };

        for (String t : terminos) {
            Paragraph p = new Paragraph(t, termFont);
            p.setSpacingAfter(3);
            document.add(p);
        }
    }

    private void agregarSeccionFirma(Document document,  Cotizacion cotizacion, String nombreUsuario) throws DocumentException {
        Paragraph espacioArriba = new Paragraph(" ");
        espacioArriba.setSpacingBefore(100f);
        document.add(espacioArriba);

        PdfPTable firmaTable = new PdfPTable(2);
        firmaTable.setWidthPercentage(100);
        firmaTable.setWidths(new float[]{1f, 1f});

        Font firmaFont = FontFactory.getFont(FUENTE, 10);
        Font labelFont = FontFactory.getFont(FUENTE, 9, Font.BOLD);

        String tipoDoc = cotizacion.getCliente().getTypeDocument();
        String nombreCliente = tipoDoc.equalsIgnoreCase("RUC")
                ? cotizacion.getCliente().getBusinessName()
                : cotizacion.getCliente().getFirstName() + " " + cotizacion.getCliente().getLastName();

        firmaTable.addCell(celdaFirma("ELABORADO POR", nombreUsuario, firmaFont, labelFont));
        firmaTable.addCell(celdaFirma("ACEPTADO POR", nombreCliente, firmaFont, labelFont));

        document.add(firmaTable);
    }

    private PdfPCell celdaFirma(String label, String nombre, Font valueFont, Font labelFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph p = new Paragraph();
        p.add(new Chunk("_________________________\n", valueFont));
        p.add(new Chunk(label + "\n", labelFont));
        p.add(new Chunk(nombre, valueFont));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);

        return cell;
    }
}
