package com.appWeb.cotizacion.pdf;
import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


@Service
@RequiredArgsConstructor
public class CotizacionPdfGeneratorService {

    private final PdfStylesUtil styles;
    private final PdfUtils pdfUtils;

    public ByteArrayInputStream generarCotizacionPDF(Cotizacion cotizacion) {
        Document document = new Document(PageSize.A4, 60, 60, 190, 130);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            Image background = pdfUtils.cargarFondo("static/img/plantilla.jpg");
            writer.setPageEvent(new PdfUtils.BackgroundPageEvent(background));

            document.open();
            String nombreUsuario = cotizacion.getUser().getNombre() + " " + cotizacion.getUser().getApellido();


            styles.agregarInformacionGeneral(document, cotizacion, nombreUsuario);
            styles.agregarTablaProductos(document, cotizacion);
            styles.agregarTotales(document, cotizacion);
            styles.agregarTerminos(document);
            styles.agregarFirma(document, cotizacion, nombreUsuario);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}