package pe.edu.vallegrande.vg_ms_marriage.application.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.vallegrande.vg_ms_marriage.domain.dto.UserMarriageDto;
import reactor.core.publisher.Mono;

public interface UserMarriageService {
    Mono<ResponseEntity<?>> addMarriage(UserMarriageDto userCommunionDto, MultipartFile[] incomeFiles, String categoryId, String typePayment);
}
