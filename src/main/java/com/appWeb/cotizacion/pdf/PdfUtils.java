package com.appWeb.cotizacion.pdf;


import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PdfUtils {

    public Image cargarFondo(String rutaRelativa) throws IOException {
        ClassPathResource resource = new ClassPathResource(rutaRelativa);
        return Image.getInstance(resource.getURL());
    }

    public static class BackgroundPageEvent extends PdfPageEventHelper {
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
}
