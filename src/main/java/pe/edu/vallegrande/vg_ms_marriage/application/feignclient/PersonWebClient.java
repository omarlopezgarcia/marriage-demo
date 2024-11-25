package pe.edu.vallegrande.vg_ms_marriage.application.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.ApplicantDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PersonWebClient {

    private final WebClient webClient;

    public PersonWebClient(@Value("${spring.client.ms-users.url}") String msUsersUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(msUsersUrl)
                .build();
    }
    

    public Mono<ApplicantDto> findById(String id) {
        return webClient.get()
                .uri("/management/user/v1/details/id/{id}", id)
                .retrieve()
                .bodyToMono(ApplicantDto.class)
                .onErrorResume(e -> {
                    log.error("Error fetching person by ID: {}", id, e);
                    return Mono.empty(); // Devuelve un Mono vacío en caso de error
                });
    }
}

