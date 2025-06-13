package com.appWeb.cotizacion.config;

import com.appWeb.cotizacion.firebaseStorage.implement.ImageMigrationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class ImageMigrationCommandRunner implements CommandLineRunner {

    @Autowired
    private ImageMigrationServiceImpl imageMigrationService;

    @Value("${app.migration.auto-run:false}")
    private boolean autoRun;

    @Value("${app.migration.batch-size:10}")
    private int batchSize;

    @Override
    public void run(String... args) throws Exception {

        if(!autoRun){
            System.out.println("Migración automática deshabilitada (app.migration.auto-run=false)");
            return;
        }
        try {
            imageMigrationService.migrateBatchImages(batchSize);

        } catch (Exception e) {
            System.err.println("❌ Error durante la migración automática: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
