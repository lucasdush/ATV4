package disk.pasta.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tabela_cliente")
public class Cliente {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(nullable = false, unique = true)
    private String cpf;
    

    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;
   
   
    @Column(nullable = true,unique = true)
    private String telefone;
    
    @OneToMany(mappedBy = "cliente", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets;

}
