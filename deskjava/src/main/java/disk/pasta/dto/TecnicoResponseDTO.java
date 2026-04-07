package disk.pasta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TecnicoResponseDTO {
   
    // retornar: id, nome, email
    
    
    private Long id;
    private String nome;
    private String email;
    private String nivel;
    private boolean ativo;
    
}



