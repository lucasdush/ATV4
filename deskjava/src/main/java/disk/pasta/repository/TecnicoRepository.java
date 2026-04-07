package disk.pasta.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import disk.pasta.model.Tecnico;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
Optional<Tecnico> findByEmail(String email);

Page<Tecnico> findAll(Pageable pageable);

}
