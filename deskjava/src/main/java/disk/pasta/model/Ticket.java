package disk.pasta.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tabela_chamado")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    
    @Column(nullable = false)
   private String titulo;
    
    @Column(length = 1000, nullable = false)
    private String descricao;

    
    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;
    
   
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @jakarta.persistence.OneToMany(mappedBy = "ticket", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Mensagem> mensagens;

    public enum Status {
    ABERTO,
    EM_ANDAMENTO,
    FECHADO
}
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Prioridade {
    BAIXA,
    MEDIA,
    ALTA
}

    public enum Nivel {
        n1,
        n2,
        n3
       
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_chamado", nullable = false)
    private Nivel nivelChamado;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    
    private LocalDateTime criado = LocalDateTime.now();
    private LocalDateTime atualizado = LocalDateTime.now();

    
}
