package com.appWeb.cotizacion.firebaseStorage;


import com.appWeb.cotizacion.model.productos.Products;

public interface ImageMigrationService {

    void migrateAllExternalImages();
    boolean migrateProductImageIfNeeded(Products product);
    void migrateBatchImages(int batchSize);

}
