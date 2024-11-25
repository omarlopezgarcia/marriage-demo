package pe.edu.vallegrande.vg_ms_marriage.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class MarriageResponseDto {
    private String idMarriage;
    private String applicantId;
    private ApplicantDto applicant; // Este campo contendrá el objeto ApplicantDto
    private List<String> incomeFileIds;
    private String comment;
    private String requestStatus;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}