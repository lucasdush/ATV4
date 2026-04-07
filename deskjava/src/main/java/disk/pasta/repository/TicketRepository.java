package disk.pasta.repository;

import java.util.List;
import java.util.Optional;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import disk.pasta.model.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findById(Long id);
    Page<Ticket> findByStatus(Ticket.Status status, Pageable pageable);

    Page<Ticket> findByTecnicoId(Long id, Pageable pageable);
    Page<Ticket> findByClienteId(Long id, Pageable pageable);

    Page<Ticket> findByNivelChamadoIn(List<Ticket.Nivel> niveis, Pageable pageable);
    
    Page<Ticket> findByStatusAndNivelChamadoIn(Ticket.Status status, List<Ticket.Nivel> niveis, Pageable pageable);
}

    

