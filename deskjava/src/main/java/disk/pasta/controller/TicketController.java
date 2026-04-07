package disk.pasta.controller;

import disk.pasta.dto.MensagemRequestDTO;
import disk.pasta.dto.MensagemResponseDTO;
import disk.pasta.dto.TicketClienteRequestDTO;
import disk.pasta.dto.TicketResponseDTO;
import disk.pasta.model.Ticket;
import disk.pasta.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // 1. Listar tickets de um cliente (Paginado)
    @PreAuthorize("hasRole('CLIENTE') and #clienteId == authentication.principal.id or hasAnyRole('N1','N2','N3','N4')") 
    @GetMapping("listar/cliente/{clienteId}")
    public ResponseEntity<Page<TicketResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ticketService.listadocliente(clienteId, pageable));
    }

    // 2. Listar todos os tickets permitidos para o nível do técnico (Paginado)
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @GetMapping("listar/tecnico/{tecnicoId}")
    public ResponseEntity<Page<TicketResponseDTO>> listarParaTecnico(
            @PathVariable Long tecnicoId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.listarPorNivelTecnico(tecnicoId, pageable));
    }

    // 3. Listar por Status (Abertos, Em Andamento, etc) + Hierarquia (Paginado)
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @GetMapping("listar/status/{status}/{tecnicoId}")
    public ResponseEntity<Page<TicketResponseDTO>> listarPorStatus(
            @PathVariable Ticket.Status status,
            @PathVariable Long tecnicoId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketService.listarPorStatus(status, tecnicoId, pageable));
    }

    // 3.1 Listar todos os tickets para técnicos (sem filtro de status)
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @GetMapping("/listar/todos")
    public ResponseEntity<Page<TicketResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ticketService.listarTodos(pageable));
    }

    // 4. Abertura de Ticket pelo Cliente
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/abrir")
    public ResponseEntity<Map<String, Object>> abrir(@Valid @RequestBody TicketClienteRequestDTO dto) {
        ticketService.abrirTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "message", "Ticket aberto com sucesso",
            "sucesso", true
        ));
    }

    // 5. Fechar Ticket
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @PutMapping("/fechar/{id}")
    public ResponseEntity<Map<String, Object>> fechar(@PathVariable Long id) {
        ticketService.fecharTicket(id);
        return ResponseEntity.ok(Map.of(
            "message", "Ticket fechado com sucesso", 
            "sucesso", true
        ));
    }

    // 5.1 Alterar Prioridade do Ticket
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @PutMapping("/{id}/prioridade/{prioridade}")
    public ResponseEntity<TicketResponseDTO> alterarPrioridade(
            @PathVariable Long id, 
            @PathVariable Ticket.Prioridade prioridade) {
        return ResponseEntity.ok(ticketService.alterarPrioridade(id, prioridade));
    }

    // 5.2 Assumir Ticket pelo Técnico
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @PutMapping("/{id}/assumir/{tecnicoId}")
    public ResponseEntity<TicketResponseDTO> assumirTicket(
            @PathVariable Long id, 
            @PathVariable Long tecnicoId) {
        return ResponseEntity.ok(ticketService.assumirTicket(id, tecnicoId));
    }

    // 5.3 Transferir Ticket
    @PreAuthorize("hasAnyRole('N1','N2','N3','N4')")
    @PutMapping("/{id}/transferir")
    public ResponseEntity<TicketResponseDTO> transferirTicket(
            @PathVariable Long id,
            @RequestParam(required = false) Long novoTecnicoId,
            @RequestParam(required = false) Ticket.Nivel novoNivel) {
        return ResponseEntity.ok(ticketService.transferirTicket(id, novoTecnicoId, novoNivel));
    }

    // 6. Enviar mensagem no chat do ticket
    @PreAuthorize("hasRole('CLIENTE') or hasAnyRole('N1','N2','N3','N4')")
    @PostMapping("/{ticketId}/mensagens")
    public ResponseEntity<MensagemResponseDTO> enviarMensagem(
            @PathVariable Long ticketId,
            @Valid @RequestBody MensagemRequestDTO dto,
            @org.springframework.security.core.annotation.AuthenticationPrincipal disk.pasta.security.CustomUserDetails principal) {
        
        String nomeRemetente = principal.getNome(); 
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.enviarMensagem(ticketId, dto.getConteudo(), nomeRemetente));
    }

    // 7. Listar histórico de mensagens do ticket
    @PreAuthorize("hasRole('CLIENTE') or hasAnyRole('N1','N2','N3','N4')")
    @GetMapping("/{ticketId}/mensagens")
    public ResponseEntity<List<MensagemResponseDTO>> listarMensagens(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ticketService.listarMensagensDoTicket(ticketId));
    }
}