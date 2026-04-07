package disk.pasta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import disk.pasta.dto.TecnicoRequestDTO;
import disk.pasta.dto.TecnicoResponseDTO;
import disk.pasta.dto.TecnicoSimplesResponseDTO;
import disk.pasta.model.Tecnico;
import disk.pasta.service.TecnicoService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoService tecnicoService;

    @PreAuthorize("hasAnyRole('N4')")
    @GetMapping("listar/completo")
    public ResponseEntity<Page<TecnicoResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        
        return ResponseEntity.ok(tecnicoService.ListarTodos(pageable));
    }
    @PreAuthorize("hasAnyRole('N1', 'N2', 'N3', 'N4')")
    public ResponseEntity<List<TecnicoSimplesResponseDTO>> listarTodosSimples() {
        return ResponseEntity.ok(tecnicoService.ListarTodosSimples());
    }
    

    // Apenas nível n4 (Admin) pode cadastrar novos técnicos
    @PreAuthorize("hasRole('N4')")
    @PostMapping("/criar")
    public ResponseEntity<Tecnico> salvar(@Valid @RequestBody TecnicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tecnicoService.salvarTecnico(dto));
    }


    // Apenas nível n4 (Admin) pode suspender (desativar) técnicos
    @PreAuthorize("hasRole('N4')")
    @PutMapping("/suspender/{id}")
    public ResponseEntity<Void> suspender(@PathVariable Long id) {
        tecnicoService.suspenderTecnico(id);
        return ResponseEntity.noContent().build();
    }

    // Apenas nível n4 pode deletar
    @PreAuthorize("hasRole('N4')")
    @DeleteMapping("deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tecnicoService.deletarTecnico(id);
        return ResponseEntity.noContent().build();
    }

    // Atualizar perfil do próprio técnico
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @PutMapping("/perfil/{id}")
    public ResponseEntity<TecnicoResponseDTO> atualizarPerfil(@PathVariable Long id, @RequestBody TecnicoRequestDTO dto) {
        return ResponseEntity.ok(tecnicoService.atualizarPerfil(id, dto));
    }


    

}