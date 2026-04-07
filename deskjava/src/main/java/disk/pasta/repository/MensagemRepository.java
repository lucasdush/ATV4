package disk.pasta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import disk.pasta.model.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findByTicketIdOrderByEnviadoEmAsc(Long ticketId);
}
