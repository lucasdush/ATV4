// LoginRequestDTO.java
package disk.pasta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class LoginResponseDTO {
    private Long id;
    private String token; // Futuro JWT
    private String nome;
    private String perfil; // "CLIENTE" ou "TECNICO"
}
