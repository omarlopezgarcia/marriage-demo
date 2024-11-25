package pe.edu.vallegrande.vg_ms_marriage.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.vallegrande.vg_ms_marriage.application.feignclient.IncomeFeignClient;
import pe.edu.vallegrande.vg_ms_marriage.application.feignclient.PersonWebClient;
import pe.edu.vallegrande.vg_ms_marriage.application.service.UserMarriageService;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.IncomeDto;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.UserMarriageDto;
import pe.edu.vallegrande.vg_ms_marriage.domain.model.Marriage;
import pe.edu.vallegrande.vg_ms_marriage.domain.repository.MarriageRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static pe.edu.vallegrande.vg_ms_marriage.application.util.Marriage.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserMarriageServiceImpl implements UserMarriageService {

    private final MarriageRepository marriageRepository;
    private final PersonWebClient personWebClient;
    private final IncomeFeignClient incomeFeignClient;
    private final ObjectMapper mapper;



    private Mono<List<String>> extractIncomeFileUrls(String responseBody) {
        try {
            IncomeDto incomeResponse = mapper.readValue(responseBody, IncomeDto.class);
            return Mono.just(incomeResponse.getFileUrls());
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public Mono<ResponseEntity<?>> addMarriage(
            UserMarriageDto userMarriageDto,
            MultipartFile[] incomeFiles,
            String categoryId,
            String typePayment
    ) {
        return personWebClient.findById(userMarriageDto.getApplicantId()) // Cambiado a findById
                .flatMap(applicantDto -> {
                    if (applicantDto != null) {
                        Marriage newMarriage = createNewMarriageEntity(userMarriageDto);

                        return marriageRepository.findFirstByDocumentNumberAndRequestStatus(userMarriageDto.getApplicantId(), PENDING)
                                .switchIfEmpty(marriageRepository.findFirstByDocumentNumberAndRequestStatus(userMarriageDto.getApplicantId(), ACCEPTED))
                                .switchIfEmpty(marriageRepository.findFirstByDocumentNumberAndRequestStatus(userMarriageDto.getApplicantId(), REJECTED))
                                .flatMap(existingMarriage -> handleExistingMarriage(existingMarriage, newMarriage, incomeFiles, categoryId, typePayment))
                                .switchIfEmpty(handleNewMarriage(newMarriage, incomeFiles, categoryId, typePayment))
                                .onErrorResume(IncorrectResultSizeDataAccessException.class, ex -> {
                                    String errorMessage = "Error al insertar el registro";
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage));
                                });
                    } else {
                        String errorMessage = "El solicitante con ID no está registrado";
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
                    }
                });
    }

    private Marriage createNewMarriageEntity(UserMarriageDto userMarriageDto) {
        Marriage newMarriage = new Marriage();
        newMarriage.setIdMarriage(UUID.randomUUID().toString());
        newMarriage.setApplicantId(userMarriageDto.getApplicantId());
        newMarriage.setRequestStatus(PENDING);
        newMarriage.setPaymentStatus(PENDING);
        newMarriage.setCreatedAt(LocalDateTime.now());
        newMarriage.setUpdatedAt(LocalDateTime.now());
        return newMarriage;
    }

    private Mono<ResponseEntity<?>> handleExistingMarriage(Marriage existingMarriage, Marriage newMarriage, MultipartFile[] incomeFiles, String categoryId, String typePayment) {        if (REJECTED.equals(existingMarriage.getRequestStatus())) {
            return createMarriageAndUploadFiles(newMarriage, incomeFiles, categoryId, typePayment)
                    .map(savedMarriage -> ResponseEntity.status(HttpStatus.CREATED).body(savedMarriage));
        } else if (ACCEPTED.equals(existingMarriage.getRequestStatus()) || PENDING.equals(existingMarriage.getRequestStatus())) {
            String errorMessage = "Ya existe una solicitud en curso";
            return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage));
        } else {
            String errorMessage = "Estado de solicitud no válido";
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage));
        }
    }

    private Mono<ResponseEntity<?>> handleNewMarriage(Marriage newMarriage, MultipartFile[] incomeFiles, String categoryId, String typePayment) {
        return createMarriageAndUploadFiles(newMarriage, incomeFiles, categoryId, typePayment)
                .map(savedMarriage -> ResponseEntity.status(HttpStatus.CREATED).body(savedMarriage));
    }

    private Mono<Marriage> createMarriageAndUploadFiles(Marriage newMarriage, MultipartFile[] incomeFiles, String categoryId, String typePayment) {
        return uploadToIncome(newMarriage, incomeFiles, categoryId, typePayment)
                .flatMap(uploadedMarriage -> marriageRepository.save(newMarriage));
    }


    private Mono<Marriage> uploadToIncome(Marriage newMarriage, MultipartFile[] files, String categoryId, String typePayment) {
        String nameProof = "ms-marriage";
        return Mono.fromCallable(() ->
                incomeFeignClient.uploadIncome(
                        files,
                        newMarriage.getApplicantId(),
                        categoryId,
                        newMarriage.getIdMarriage(),
                        nameProof,
                        typePayment
                )
        ).flatMap(responseEntity -> {
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return extractIncomeFileUrls(responseEntity.getBody())
                        .doOnNext(newMarriage::setIncomeFileIds)
                        .thenReturn(newMarriage);
            } else {
                return Mono.error(new RuntimeException("No se pudieron cargar archivos al servicio de ingresos"));
            }
        });
    }
}
