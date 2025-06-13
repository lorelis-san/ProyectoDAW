package com.appWeb.cotizacion.firebaseStorage.implement;

import com.appWeb.cotizacion.firebaseStorage.FirebaseStorageService;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    private static final String FOLDER_NAME = "products";
    private static final int MAX_RETRIES = 3;
    private static final int CONNECTION_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 30000;

    @Value("${firebase.bucket-name}")
    private String bucketName;
    @Autowired
    private Storage storage;

    @PostMapping
    public void initializeFirebaseStorage(){
        verifyBucketConfiguration();
        testStorageConnection();
    }

    private void verifyBucketConfiguration() {
        try {
            String actualBucketName = FirebaseApp.getInstance().getOptions().getStorageBucket();
            System.out.println("Firebase bucket: " + actualBucketName);
            System.out.println("App bucket: " + bucketName);

            if(!actualBucketName.equals(bucketName)){
                System.out.println("Error: Los nombres no conciden");
            }
        }catch (Exception e){
            System.err.println("Error al verificar el nombre del bucket: " + e.getMessage());
        }
    }

    private void testStorageConnection() {
        try {
            storage.list().iterateAll().forEach(bucket ->
                    System.out.println(" - " + bucket.getName()));
            storage.list(bucketName).iterateAll().forEach(blob ->
                    System.out.println(" - " + blob.getName()));
            System.out.println("Firebase Storage connection successful");

        }catch (Exception e){
            System.err.println("Error al conectar con Firebase Storage: " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        validateFile(file);

        String fileName = generateFileName(Objects.requireNonNull(file.getOriginalFilename()));
        String path = FOLDER_NAME + "/" + fileName;

        return uploadToFirebase(file.getBytes(), file.getContentType(), path);
    }

    @Override
    public String uploadFileFromUrl(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IOException("La URL no puede estar vacia");
        }
        String cleanUrl = cleanImageUrl(imageUrl);
        return downloadAndUpload(cleanUrl);
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("No se puede cargar un archivo vacío");
        }
    }

    private String generateFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + fileExtension;
    }

    private String uploadToFirebase(byte[] fileBytes, String contentType, String path) throws IOException {
        try {
            String downloadToken = UUID.randomUUID().toString();

            Map<String, String> metadata = new HashMap<>();
            metadata.put("firebaseStorageDownloadTokens", downloadToken);

            BlobId blobId = BlobId.of(bucketName, path);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .setMetadata(metadata)
                    .build();
            storage.create(blobInfo, fileBytes);

            String downloadUrl = buildDownloadUrl(path, downloadToken);
            System.out.println("Archivo subido correctamente: " + path);

            return downloadUrl;
        } catch (Exception e) {
            System.err.println("Error al subir el archivo: " + e.getMessage());
            throw new IOException("Error al subir el archivo Firebase: " + e.getMessage());
        }
    }

    private String buildDownloadUrl(String path, String downloadToken) {
        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media&token=%s",
                bucketName,
                URLEncoder.encode(path, StandardCharsets.UTF_8),
                downloadToken
        );
    }

    private String cleanImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return imageUrl;
        }
        String cleanUrl = imageUrl.split(",")[0].trim();
        System.out.println("Original URL: " + imageUrl);
        System.out.println("URL limpia: " + cleanUrl);
        return cleanUrl;
    }

    private String downloadAndUpload(String imageUrl) throws IOException {
        IOException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return performDownload(imageUrl);
            } catch (IOException e) {
                lastException = e;
                if (e.getMessage().contains("FORBIDDEN_403")) {
                    System.err.println("Stopping retries - Server blocking access (403)");
                    break;
                }
                if (attempt == MAX_RETRIES) break;
                try {
                    Thread.sleep(1000 * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Descarga interrumpida", ie);
                }
            }
        }
        throw new IOException("a descarga falló después de " + MAX_RETRIES + " intentos: " + lastException.getMessage(), lastException);
    }

    private String performDownload(String imageUrl) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            setupConnection(connection, imageUrl);

            int responseCode = connection.getResponseCode();
            if (responseCode == 403) {
                throw new IOException("Access forbidden (403) - Server is blocking the request. URL may be protected against scraping.");
            } else if (responseCode == 404) {
                throw new IOException("Image not found (404) - The image may have been moved or deleted.");
            } else if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Error downloading image, response code: " + responseCode +
                        " - " + connection.getResponseMessage());
            }

            String contentType = connection.getContentType();
            validateContentType(contentType);
            inputStream = connection.getInputStream();
            byte[] imageBytes = inputStream.readAllBytes();

            if (imageBytes.length == 0) {
                throw new IOException("Could not read image bytes");
            }

            String fileName = UUID.randomUUID().toString() + getFileExtension(contentType);
            String path = FOLDER_NAME + "/" + fileName;

            return uploadToFirebase(imageBytes, contentType, path);
        } finally {
            if (inputStream != null) inputStream.close();
            if (connection != null) connection.disconnect();
        }
    }

    private void setupConnection(HttpURLConnection connection, String imageUrl) throws ProtocolException {
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setInstanceFollowRedirects(true);

        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        connection.setRequestProperty("Accept", "image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "es-MX,es;q=0.9,en;q=0.8");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Referer", getReferer(imageUrl));

        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Sec-Fetch-Dest", "image");
        connection.setRequestProperty("Sec-Fetch-Mode", "no-cors");
        connection.setRequestProperty("Cache-Control", "no-cache");
    }

    private void validateContentType(String contentType) throws IOException {
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("El contenido no es una imagen válida, tipo de contenido: " + contentType);
        }
    }

    private String getReferer(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String host = url.getHost();

            if (host.contains("ford.mx")) return "https://www.ford.mx/";
            if (host.contains("ford.com")) return "https://www.ford.com/";

            return url.getProtocol() + "://" + host + "/";
        } catch (Exception e) {
            return "https://www.google.com/";
        }
    }

    private String extractPathFromUrl(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/o/");
            if (parts.length > 1) {
                String pathAndToken = parts[1];
                String pathEncoded = pathAndToken.split("\\?")[0];
                return URLDecoder.decode(pathEncoded, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            System.err.println("No se pudo extraer el path de la URL: " + e.getMessage());
        }
        return null;
    }

    private String getFileExtension(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/bmp" -> ".bmp";
            default -> ".jpg";
        };
    }
    @Override
    public void deleteFile(String imageUrl) {
        try {
            String path = extractPathFromUrl(imageUrl);
            if (path == null) {
                System.err.println("No se pudo extraer la ruta de la URL para su eliminación");
                return;
            }
            BlobId blobId = BlobId.of(bucketName, path);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                System.out.println("Archivo eliminado correctamente: " + path);
            } else {
                System.err.println("No se pudo eliminar el archivo (may not exist): " + path);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar el archivo de Firebase:: " + e.getMessage());
        }
    }
}