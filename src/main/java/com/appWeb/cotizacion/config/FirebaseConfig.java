package com.appWeb.cotizacion.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-file}")
    private String firebaseConfigPath;

    @Value("${firebase.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setStorageBucket(bucketName)
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase inicializado correctamente con bucket: " + bucketName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar Firebase", e);
        }
    }

    @Bean
    public Bucket firebaseBucket() {
        return StorageClient.getInstance().bucket();
    }

    @Bean
    public Storage storage() throws IOException {
        InputStream serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(extractProjectId())
                .build()
                .getService();
    }

    private String extractProjectId() {
        if (bucketName != null && bucketName.contains(".")) {
            return bucketName.substring(0, bucketName.indexOf("."));
        }
        return null;
    }



}
