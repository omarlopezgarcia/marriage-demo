package pe.edu.vallegrande.vg_ms_marriage.domain.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.vg_ms_marriage.domain.model.Marriage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MarriageRepository extends ReactiveMongoRepository<Marriage, String> {
    Flux<Marriage> findByRequestStatus(String requestStatus);
    Mono<Marriage> findFirstByDocumentNumberAndRequestStatus(String documentNumber, String requestStatus);
}
