package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

public class MainHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent input, Context context) {
        if ("image".equalsIgnoreCase(input.getRawPath().replace("/", ""))) {
            String imageBase64 = getImage("/white-bird.png");
            if (imageBase64 != null) {
                return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(200)
                        .withHeaders(Map.of("Content-Type", "image/png"))
                        .withBody(imageBase64)
                        .withIsBase64Encoded(true)
                        .build();
            } else {
                return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(404)
                        .withHeaders(Map.of("Content-Type", "application/json"))
                        .withBody(GSON.toJson(Map.of("status", 404, "message", "Image not found")))
                        .build();
            }
        }

        var body = Map.of("status", 200, "message", "ok");
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(GSON.toJson(body))
                .build();
    }

    public String getImage(String path) {
        try (InputStream inputStream = MainHandler.class.getResourceAsStream(path)) {
            if (inputStream != null) {
                byte[] imageBytes = convertInputStreamToByteArray(inputStream);
                return Base64.getEncoder().encodeToString(imageBytes);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image", e);
        }
    }

    private byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}

