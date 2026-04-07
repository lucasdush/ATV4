package disk.pasta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClienteRequestDTO {

    //Valida a entrada de dados do usuario conforme os criterios.
    //A api aceitará os daods somente se atender os criterios.
    // Casa contrario retorna em jsonn uma das mensagens abaixo.

    @NotBlank(message = "O cpf é obrigatório")
    private String cpf;
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, message = "O nome deve ter no mínimo 3 caracteres")
    private String nome;

    
    @NotBlank(message = "O email é obrigatório")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    private String telefone;
    
    
    


}
