package edu.team.carshopbackend.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Component
public class PhotoClient {

    private final RestClient restClient;

    public PhotoClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8000")
                .build();
    }

    public List<UploadPhotoResponse> uploadPhotos(
            Long carId,
            List<MultipartFile> files
    ) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        for (MultipartFile file : files) {
            builder.part("files", file.getResource())
                    .filename(Objects.requireNonNullElse(file.getOriginalFilename(), "img"))
                    .contentType(
                            MediaType.parseMediaType(file.getContentType())
                    );
        }

        return restClient.post()
                .uri("/cars/{carId}/photos", carId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
