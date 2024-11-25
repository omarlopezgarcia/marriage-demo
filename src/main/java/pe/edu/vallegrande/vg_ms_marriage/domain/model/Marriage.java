package pe.edu.vallegrande.vg_ms_marriage.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "marriage")
public class Marriage {
    @Id
    private String idMarriage;
    private List<String> storageId;
    private List<String> incomeFileIds;
    private String applicantId;
    private String comment;
    private String paymentStatus;
    private String requestStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
