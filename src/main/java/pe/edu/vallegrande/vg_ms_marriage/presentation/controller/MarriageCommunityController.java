package pe.edu.vallegrande.vg_ms_marriage.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.vallegrande.vg_ms_marriage.application.service.impl.UserMarriageServiceImpl;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/community/marriage/v1")
public class MarriageCommunityController {

    private final UserMarriageServiceImpl userMarriageService;

    @Autowired
    public MarriageCommunityController(UserMarriageServiceImpl userMarriageService) {
        this.userMarriageService = userMarriageService;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<?>> addMarriage(
            @ModelAttribute UserMarriageDto userMarriageDto,
            @RequestPart(name = "fileIncome", required = false) MultipartFile[] incomeFiles,
            @RequestPart(name = "categoryId", required = false) String categoryId,
            @RequestPart(name = "typePayment", required = false) String typePayment // Nuevo campo
    ) {

        MultipartFile[] finalIncomeFiles = incomeFiles != null ? incomeFiles : new MultipartFile[0];
        String finalCategoryId = (categoryId != null && !categoryId.trim().isEmpty()) ? categoryId : "default-category-id";
        String finalTypePayment = (typePayment != null && !typePayment.trim().isEmpty()) ? typePayment : "default-type";
        return userMarriageService.addMarriage(userMarriageDto, finalIncomeFiles, finalCategoryId, finalTypePayment);
    }

}
