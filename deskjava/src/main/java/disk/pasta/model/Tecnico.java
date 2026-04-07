package disk.pasta.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "tecnicos")

public class Tecnico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nome;
   
    @Column(nullable = false)
    private String senha;
    
    public enum Nivel {
        n1,
        n2,
        n3,
        n4
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nivel nivel;

    @Column(nullable = false)
    private boolean ativo = true;

     @OneToMany(mappedBy = "tecnico")
    private List<Ticket> tickets;
    

}
