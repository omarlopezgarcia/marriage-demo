package pe.edu.vallegrande.vg_ms_marriage.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
