package com.appWeb.cotizacion.controller;


import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.pdf.CotizacionPdfGeneratorService;
import com.appWeb.cotizacion.service.cotizacion.CotizacionService;
import com.appWeb.cotizacion.service.cotizacion.PdfGeneratorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    @Autowired
    private CotizacionService cotizacionService;

    @Autowired
    private CotizacionPdfGeneratorService cotizacionPdfGeneratorService;

    @GetMapping("/cotizacion/{id}")
    public ResponseEntity<InputStreamResource> generarPDF(@PathVariable Long id) {
        Cotizacion cotizacion = cotizacionService.obtenerPorId(id);
        ByteArrayInputStream bis = cotizacionPdfGeneratorService.generarCotizacionPDF(cotizacion);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + cotizacion.getNumeroCotizacion()+"-"+ cotizacion.getVehiculo().getPlaca()+"-"+cotizacion.getVehiculo().getMarca() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
