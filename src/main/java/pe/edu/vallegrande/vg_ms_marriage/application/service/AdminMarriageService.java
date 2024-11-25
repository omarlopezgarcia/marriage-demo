package pe.edu.vallegrande.vg_ms_marriage.application.service;

import pe.edu.vallegrande.vg_ms_marriage.domain.dto.AdminMarriageDto;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.MarriageResponseDto;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.PaymentStatusDto;
import pe.edu.vallegrande.vg_ms_marriage.domain.model.Marriage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminMarriageService {
    Flux<MarriageResponseDto> listAllRequest(String requestStatus);
    Mono<Marriage> updateMarriage(String idMarriage, AdminMarriageDto adminMarriageDto);
    Mono<Marriage> updatePaymentStatus(String id, PaymentStatusDto paymentStatusDto);
}