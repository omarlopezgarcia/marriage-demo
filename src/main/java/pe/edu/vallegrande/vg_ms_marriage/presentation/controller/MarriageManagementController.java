package pe.edu.vallegrande.vg_ms_marriage.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_marriage.application.service.impl.AdminMarriageServiceImpl;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.*;
import pe.edu.vallegrande.vg_ms_marriage.domain.model.Marriage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/management/marriage/v1")
public class MarriageManagementController {

    private final AdminMarriageServiceImpl marriageService;

    @Autowired
    public MarriageManagementController(AdminMarriageServiceImpl marriageService) {
        this.marriageService = marriageService;
    }

    @GetMapping("/list")
    public Flux<MarriageResponseDto> listAllRequest(@RequestParam(required = false) String requestStatus) {
        return marriageService.listAllRequest(requestStatus);
    }

    @PatchMapping("/update/{id}")
    public Mono<Marriage> updateMarriage(@PathVariable String id, @RequestBody AdminMarriageDto adminMarriageDto) {
        return marriageService.updateMarriage(id, adminMarriageDto);
    }

    @PatchMapping("/updatePayment/{id}")
    public Mono<Marriage> updatePaymentStatus(@PathVariable String id, @RequestBody PaymentStatusDto paymentStatusDto) {
        return marriageService.updatePaymentStatus(id, paymentStatusDto);
    }

    @GetMapping("/person/{applicantId}")
    public Mono<ApplicantDto> getPersonById(@PathVariable String applicantId) {
        return marriageService.findById(applicantId);
    }
}
