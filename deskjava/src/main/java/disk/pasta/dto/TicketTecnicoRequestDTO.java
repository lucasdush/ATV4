package disk.pasta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketTecnicoRequestDTO {

    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotBlank(message = "Defina a prioridade (BAIXA, MEDIA, ALTA)")
    private String prioridade;

    @NotBlank(message = "Defina o nível (n1, n2, n3")
    private String nivel;

    // Opcional: O técnico pode abrir o ticket já atribuindo a outro técnico
    private Long tecnicoId;
    
  
}