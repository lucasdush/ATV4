

package disk.pasta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClienteSimplesResponseDTO {
    //Define campos que serao exibidos.
    // Após consulta no banco de dados, retornarão: id, nome, email, senha.
    //Omite o id e senha da tabela.

    private String nome;
    
 
}