package disk.pasta.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensagemResponseDTO {
    private Long id;
    private String conteudo;
    private String remetenteNome;
    private LocalDateTime enviadoEm;
}