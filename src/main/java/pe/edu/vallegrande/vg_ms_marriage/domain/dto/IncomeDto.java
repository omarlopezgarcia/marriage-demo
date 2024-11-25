package pe.edu.vallegrande.vg_ms_marriage.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class IncomeDto {
    private String personId;
    private String categoryId;
    private String proofId;
    private String nameProof;
    private List<String> fileUrls;
    private String typePayment;
}