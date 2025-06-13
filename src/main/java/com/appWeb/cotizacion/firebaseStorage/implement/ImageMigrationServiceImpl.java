package com.appWeb.cotizacion.firebaseStorage.implement;

import com.appWeb.cotizacion.firebaseStorage.FirebaseStorageService;
import com.appWeb.cotizacion.firebaseStorage.ImageMigrationService;
import com.appWeb.cotizacion.model.productos.Products;
import com.appWeb.cotizacion.repository.productos.ProductsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageMigrationServiceImpl implements ImageMigrationService {

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Transactional
    public void migrateAllExternalImages(){

        List<Products> products = productsRepository.findAll();
        int totalProducts = products.size();
        int migrated  = 0, errors = 0;

        for (int i = 0; i < totalProducts; i++) {
            Products product = products.get(i);
            try {
                if (migrateProductImageIfNeeded(products.get(i))) {
                    migrated++;
                }
            }catch (Exception e){
                errors++;
                logError(products.get(i).getId(), e);
            }
            pauseIfNeeded(i, 10, 1000);
        }
        System.out.printf("Migración completada: %d migradas, %d errores de %d productos totales%n",
                migrated, errors, totalProducts);
    }

    @Transactional
    public boolean migrateProductImageIfNeeded(Products product) {
        String imageUrl = product.getImageUrl();

        if (!needsMigration(imageUrl)) return false;

        try {
            System.out.println("Migrando imagen para producto ID: " + product.getId() + " - URL: " + imageUrl);
            String firebaseUrl = firebaseStorageService.uploadFileFromUrl(imageUrl);
            product.setImageUrl(firebaseUrl);
            productsRepository.save(product);
            System.out.println("Imagen migrada exitosamente: " + firebaseUrl);
            return true;

        } catch (IOException e) {
            logError(product.getId(), e);
            throw new RuntimeException("Error en migración: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public void migrateBatchImages(int batchSize) {
        System.out.println("Iniciando migración por lotes con tamaño: " + batchSize);

        int offset = 0;
        while (true) {
            List<Products> batch = getProductsNeedingMigration(offset, batchSize);
            if (batch.isEmpty()) break;

            System.out.printf("Procesando lote: productos %d-%d%n", offset + 1, offset + batch.size());

            for (Products product : batch) {
                try {
                    migrateProductImageIfNeeded(product);
                } catch (Exception e) {
                    logError(product.getId(), e);
                }
            }
            offset += batchSize;
            pauseIfNeeded(offset, batchSize, 2000);
        }
        System.out.println("Migración por lotes completada");
    }

    private List<Products> getProductsNeedingMigration(int offset, int limit) {
        return productsRepository.findAll().stream()
                .filter(p -> needsMigration(p.getImageUrl()))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private boolean needsMigration(String imageUrl) {
        return imageUrl != null &&
                !imageUrl.trim().isEmpty() &&
                !imageUrl.contains("firebasestorage.googleapis.com") &&
                !imageUrl.startsWith("data:") &&
                !imageUrl.startsWith("file:") &&
                !imageUrl.startsWith("localhost") &&
                !imageUrl.startsWith("127.0.0.1");
    }

    private void pauseIfNeeded(int index, int interval, int millis) {
        if (index % interval == 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void logError(Long productId, Exception e) {
        System.err.printf("Error en producto ID %d: %s%n", productId, e.getMessage());
    }
}
