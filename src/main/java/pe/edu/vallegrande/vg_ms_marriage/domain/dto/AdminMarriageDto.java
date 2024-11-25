package pe.edu.vallegrande.vg_ms_marriage.domain.dto;

import lombok.Data;


@Data
public class AdminMarriageDto {
    private String priest;
    private String marriageDate;
    private String requestStatus;
    private String paymentStatus;
}
