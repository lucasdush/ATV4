package disk.pasta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TicketResponseDTO {
    //Define campos que serao exibidos.
    // Após consulta no banco de dados, retornarão: id, nome, email, senha.
    //Omite o id e senha da tabela.

    private long id;
    
    private String titulo;
    
    private String descricao;
    
    private String status;

    private ClienteSimplesResponseDTO cliente;

    private String prioridade;

    private String tecnicoNome;

    

    
}
