package pe.edu.vallegrande.vg_ms_marriage.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.*;
import pe.edu.vallegrande.vg_ms_marriage.domain.model.Marriage;
import pe.edu.vallegrande.vg_ms_marriage.domain.repository.MarriageRepository;
import pe.edu.vallegrande.vg_ms_marriage.application.feignclient.PersonWebClient;
import pe.edu.vallegrande.vg_ms_marriage.application.service.AdminMarriageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static pe.edu.vallegrande.vg_ms_marriage.application.util.Marriage.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminMarriageServiceImpl implements AdminMarriageService {

    private final MarriageRepository marriageRepository;
    private final PersonWebClient personWebClient;


    public Mono<ApplicantDto> findById(String applicantId) {
        return personWebClient.findById(applicantId)
                .flatMap(response -> response != null ? Mono.just(response) : Mono.error(new RuntimeException("No se encontró la persona")))
                .onErrorResume(e -> {
                    log.error("Error al obtener persona por ID: {}", applicantId, e);
                    return Mono.empty();
                });
    }

    @Override
    public Flux<MarriageResponseDto> listAllRequest(String requestStatus) {
        Flux<Marriage> marriages = (requestStatus == null || requestStatus.isEmpty())
                ? marriageRepository.findAll()
                : marriageRepository.findByRequestStatus(requestStatus);

        return marriages.flatMap(marriage ->
                findById(marriage.getApplicantId())
                        .flatMap(applicantDto -> {
                            MarriageResponseDto responseDto = new MarriageResponseDto();
                            responseDto.setIdMarriage(marriage.getIdMarriage());
                            responseDto.setApplicantId(marriage.getApplicantId());
                            responseDto.setIncomeFileIds(marriage.getIncomeFileIds());
                            responseDto.setPaymentStatus(marriage.getPaymentStatus());
                            responseDto.setComment(marriage.getComment());
                            responseDto.setRequestStatus(marriage.getRequestStatus());
                            responseDto.setCreatedAt(marriage.getCreatedAt());
                            responseDto.setUpdatedAt(marriage.getUpdatedAt());
                            responseDto.setApplicant(applicantDto);
                            return Mono.just(responseDto);
                        })
        );
    }


    @Override
    public Mono<Marriage> updateMarriage(String idMarriage, AdminMarriageDto adminMarriageDto) {
        return marriageRepository.findById(idMarriage)
                .flatMap(marriage -> {
                    if (marriage.getRequestStatus().equals(PENDING) &&
                            adminMarriageDto.getRequestStatus() != null &&
                            (adminMarriageDto.getRequestStatus().equals(ACCEPTED)
                                    || adminMarriageDto.getRequestStatus().equals(REJECTED))) {
                        marriage.setRequestStatus(adminMarriageDto.getRequestStatus());
                        return marriageRepository.save(marriage);
                    } else if (marriage.getRequestStatus().equals(ACCEPTED)
                            || marriage.getRequestStatus().equals(REJECTED)) {
                        log.info("No se hace ninguna acción porque ya está en un estado final {}", marriage.getRequestStatus());
                        return Mono.just(marriage);
                    } else {

                        if (adminMarriageDto.getPaymentStatus() != null) {
                            marriage.setPaymentStatus(adminMarriageDto.getPaymentStatus());
                        }

                        if (adminMarriageDto.getRequestStatus() != null) {
                            marriage.setRequestStatus(adminMarriageDto.getRequestStatus());
                        }
                        return marriageRepository.save(marriage);
                    }
                });
    }

    @Override
    public Mono<Marriage> updatePaymentStatus(String id, PaymentStatusDto paymentStatusDto) {
        return marriageRepository.findById(id)
                .flatMap(marriage -> {
                    if (marriage.getRequestStatus().equals(ACCEPTED) || marriage.getRequestStatus().equals(REJECTED)) {
                        log.info("No se hace ninguna acción porque ya está en un estado final {}", marriage.getRequestStatus());
                        return Mono.just(marriage);
                    }
                    if (marriage.getPaymentStatus().equals(ACCEPTED) || marriage.getPaymentStatus().equals(REJECTED)) {
                        log.info("No se puede cambiar el PaymentStatus porque ya está en un estado final {}", marriage.getPaymentStatus());
                        return Mono.just(marriage);
                    }
                    if (paymentStatusDto.getPaymentStatus() != null &&
                            (paymentStatusDto.getPaymentStatus().equals(ACCEPTED)
                                    || paymentStatusDto.getPaymentStatus().equals(REJECTED))) {
                        marriage.setPaymentStatus(paymentStatusDto.getPaymentStatus());
                        return marriageRepository.save(marriage);
                    } else {
                        return Mono.error(new IllegalArgumentException("El PaymentStatus debe ser 'A' o 'R'"));
                    }
                });
    }
}
