package disk.pasta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MensagemRequestDTO {
    @NotBlank(message = "O conteúdo da mensagem não pode estar vazio")
    private String conteudo;
}