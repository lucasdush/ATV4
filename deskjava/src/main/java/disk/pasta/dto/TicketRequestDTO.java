package disk.pasta.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TicketRequestDTO {

    //Valida a entrada de dados do usuario conforme os criterios.
    //A api aceitará os daods somente se atender os criterios.
    // Casa contrario retorna em jsonn uma das mensagens abaixo.


    private Long clienteId;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

   
    private String prioridade;
    
    


}
