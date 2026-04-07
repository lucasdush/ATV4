package disk.pasta.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import disk.pasta.dto.ClienteRequestDTO;
import disk.pasta.dto.ClienteResponseDTO;
import disk.pasta.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    //controller do post(criar)
    
    @PostMapping("/criar")
    public ResponseEntity<Map<String, Object>> salvar(@Valid @RequestBody ClienteRequestDTO dto){
        clienteService.salvarCliente(dto);
        return ResponseEntity
            .created(null)
            .body(Map.of("Message", "Cadastramento com sucesso", "Sucesso", true));

        
    }

    //controller do get(buscar)
   @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @GetMapping("listar")
    public ResponseEntity<Page<ClienteResponseDTO>> listar(@PageableDefault(size = 10, sort = "nome") Pageable pageable){
        return ResponseEntity
        .ok()
        .body(clienteService.ListarTodos(pageable));
    }

    //controller do get(atualizar)
    @PreAuthorize("hasAnyRole('N4','CLIENTE')")
    @PutMapping("atualizar/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(
        @PathVariable Long id,
        @RequestBody ClienteRequestDTO dto){
            return ResponseEntity.ok(clienteService.atualizarCliente(id, dto));
        }

    @PreAuthorize("hasAnyRole('N4','CLIENTE')")  
    @DeleteMapping("deletar/{id}")
    public ResponseEntity<Map<String, Object>> deletarCliente (@PathVariable Long id){
        clienteService.deletarCliente(id);
        return ResponseEntity
        .ok()
        .body(Map.of("menssage","Excluido com sucesso", "Sucesso", true));
    }

}
