package disk.pasta.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import disk.pasta.dto.TecnicoResponseDTO;
import disk.pasta.dto.TecnicoSimplesResponseDTO;
import disk.pasta.dto.TecnicoRequestDTO;
import disk.pasta.model.Tecnico;
import disk.pasta.repository.TecnicoRepository;


@Service
public class TecnicoService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

public Page<TecnicoResponseDTO> ListarTodos(Pageable pageable) {
        return tecnicoRepository.findAll(pageable)
            .map(u -> new TecnicoResponseDTO(u.getId(), u.getNome(), u.getEmail(), u.getNivel().name(), u.isAtivo()));
    }

    // Se precisar de uma listagem simples para selects/dropdowns (sem paginação)
    public List<TecnicoSimplesResponseDTO> ListarTodosSimples() {
        return tecnicoRepository.findAll().stream()
            .filter(t -> t.getNivel() != Tecnico.Nivel.n4)
            .map(u -> new TecnicoSimplesResponseDTO(u.getNome()))
            .toList();
    }
    

    // Salvar um novo cliente. 
    public Tecnico salvarTecnico(TecnicoRequestDTO dto){
        if(tecnicoRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("email já cadastrado");
        }

        
        Tecnico novoTecnico = new Tecnico();
        novoTecnico.setNome(dto.getNome());
        novoTecnico.setEmail(dto  .getEmail());
        // Criptografar a senha antes de salvar
        novoTecnico.setSenha(bCryptPasswordEncoder.encode(dto.getSenha()));
        novoTecnico.setNivel(Tecnico.Nivel.valueOf(dto.getNivel().toLowerCase()));
        novoTecnico.setAtivo(true); // Define o técnico como ativo por padrão    
        return tecnicoRepository.save(novoTecnico);
        
    }

    public Tecnico atualizarTecnico(Long id, TecnicoRequestDTO dto) {
    Tecnico tecnico = tecnicoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Tecnico não encontrado"));

    tecnico.setNome(dto.getNome());
    tecnico.setEmail(dto.getEmail());
    tecnico.setSenha(bCryptPasswordEncoder.encode(dto.getSenha()));
    tecnico.setNivel(Tecnico.Nivel.valueOf(dto.getNivel().toLowerCase()));
    return tecnicoRepository.save(tecnico);
    }

    public void deletarTecnico(Long id) {
        if (!tecnicoRepository.existsById(id)) {
            throw new RuntimeException("Tecnico não encontrado");
        }
        tecnicoRepository.deleteById(id);
    }

    public TecnicoResponseDTO atualizarPerfil(Long id, TecnicoRequestDTO dto) {
        Tecnico tecnico = tecnicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
        
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            tecnico.setNome(dto.getNome());
        }
        
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            tecnico.setSenha(bCryptPasswordEncoder.encode(dto.getSenha()));
        }
        
        Tecnico updated = tecnicoRepository.save(tecnico);
        return new TecnicoResponseDTO(updated.getId(), updated.getNome(), updated.getEmail(), updated.getNivel().name(), updated.isAtivo());
    }

  public void suspenderTecnico(Long id) {
    Tecnico tecnico = tecnicoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
    
    tecnico.setAtivo(false); // Seta para inativo
    tecnicoRepository.save(tecnico);
}

}

