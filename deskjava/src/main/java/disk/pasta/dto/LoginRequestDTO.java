package disk.pasta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "O email não pode estar vazio")
    private String email;

    @NotBlank(message = "A senha não pode estar vazia")
    private String senha;
}