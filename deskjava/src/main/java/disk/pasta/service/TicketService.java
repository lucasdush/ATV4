package disk.pasta.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import disk.pasta.dto.ClienteSimplesResponseDTO;
import disk.pasta.dto.MensagemResponseDTO;
import disk.pasta.dto.TicketClienteRequestDTO;
import disk.pasta.dto.TicketResponseDTO;
import disk.pasta.dto.TicketTecnicoRequestDTO;
import disk.pasta.model.Cliente;
import disk.pasta.model.Mensagem;
import disk.pasta.model.Tecnico;
import disk.pasta.model.Ticket;
import disk.pasta.repository.ClienteRepository;
import disk.pasta.repository.MensagemRepository;
import disk.pasta.repository.TecnicoRepository;
import disk.pasta.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    // 1. Listagem do Cliente (Paginada)
    public Page<TicketResponseDTO> listadocliente(Long clienteId, Pageable pageable) {
        clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return ticketRepository.findByClienteId(clienteId, pageable)
            .map(this::convertToDTO);
    }

    // 2. Listar por Nível do Técnico (Paginada)
    public Page<TicketResponseDTO> listarPorNivelTecnico(Long tecnicoId, Pageable pageable) {
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));

        List<Ticket.Nivel> niveisPermitidos = Arrays.stream(Ticket.Nivel.values())
            .filter(n -> n.ordinal() <= tecnico.getNivel().ordinal())
            .collect(Collectors.toList());

        return ticketRepository.findByNivelChamadoIn(niveisPermitidos, pageable)
            .map(this::convertToDTO);
    }

    // 3. Listar por Status + Hierarquia (Paginada)
    public Page<TicketResponseDTO> listarPorStatus(Ticket.Status status, Long tecnicoId, Pageable pageable) {
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));

        List<Ticket.Nivel> niveisPermitidos = Arrays.stream(Ticket.Nivel.values())
            .filter(n -> n.ordinal() <= tecnico.getNivel().ordinal())
            .toList();

        return ticketRepository.findByStatusAndNivelChamadoIn(status, niveisPermitidos, pageable)
            .map(this::convertToDTO);
    }

    // Listar todos para técnico baseado no nível
    public Page<TicketResponseDTO> listarTodos(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(this::convertToDTO);
    }

    // Método Auxiliar de Conversão
    private TicketResponseDTO convertToDTO(Ticket t) {
        return new TicketResponseDTO(
            t.getId(),
            t.getTitulo(),
            t.getDescricao(),
            t.getStatus().name(),
            t.getCliente() != null ? new ClienteSimplesResponseDTO(t.getCliente().getNome()) : null,
            t.getPrioridade().name(),
            t.getTecnico() != null ? t.getTecnico().getNome() : "Não atribuído"
        );
    }

    public TicketResponseDTO assumirTicket(Long id, Long tecnicoId) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));

        ticket.setTecnico(tecnico);
        // Ao assumir, se estiver ABERTO, move para EM_ANDAMENTO
        if (ticket.getStatus() == Ticket.Status.ABERTO) {
            ticket.setStatus(Ticket.Status.EM_ANDAMENTO);
        }
        ticket.setAtualizado(java.time.LocalDateTime.now());
        
        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    public TicketResponseDTO transferirTicket(Long id, Long novoTecnicoId, Ticket.Nivel novoNivel) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        if (novoTecnicoId != null) {
            Tecnico tecnico = tecnicoRepository.findById(novoTecnicoId)
                .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
            ticket.setTecnico(tecnico);
        } else {
            // Se não passar técnico, o ticket volta a ficar sem responsável
            ticket.setTecnico(null);
            ticket.setStatus(Ticket.Status.ABERTO);
        }

        if (novoNivel != null) {
            ticket.setNivelChamado(novoNivel);
        }

        ticket.setAtualizado(java.time.LocalDateTime.now());
        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    // --- MÉTODOS DE OPERAÇÃO ---

    public Ticket abrirTicket(TicketClienteRequestDTO ticketRequestDTO) {
        Ticket novoTicket = new Ticket();
        novoTicket.setTitulo(ticketRequestDTO.getTitulo());
        novoTicket.setDescricao(ticketRequestDTO.getDescricao());
        
        if (ticketRequestDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(ticketRequestDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            novoTicket.setCliente(cliente);
        }

        novoTicket.setPrioridade(Ticket.Prioridade.BAIXA);
        novoTicket.setStatus(Ticket.Status.ABERTO);
        novoTicket.setNivelChamado(Ticket.Nivel.n1); // Default level
        
        return ticketRepository.save(novoTicket);
    }

    public Ticket tecnicoAbreTicket(TicketTecnicoRequestDTO dto, Long tecnicoId) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico não encontrado."));

        Ticket novoTicket = new Ticket();
        novoTicket.setTitulo(dto.getTitulo());
        novoTicket.setDescricao(dto.getDescricao());
        novoTicket.setCliente(cliente);
        novoTicket.setTecnico(tecnico);
        novoTicket.setStatus(Ticket.Status.EM_ANDAMENTO);
        novoTicket.setPrioridade(Ticket.Prioridade.valueOf(dto.getPrioridade().toUpperCase()));
        novoTicket.setNivelChamado(Ticket.Nivel.valueOf(dto.getNivel().toLowerCase()));

        return ticketRepository.save(novoTicket);
    }

    public Ticket fecharTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        ticket.setStatus(Ticket.Status.FECHADO);
        return ticketRepository.save(ticket);
    }

    public TicketResponseDTO alterarPrioridade(Long id, Ticket.Prioridade prioridade) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        ticket.setPrioridade(prioridade);
        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    public Ticket transferirTicket(Long id, Long tecnicoId) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        Tecnico novoTecnico = tecnicoRepository.findById(tecnicoId)
            .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));
        
        ticket.setTecnico(novoTecnico);
        return ticketRepository.save(ticket);
    }

    public MensagemResponseDTO enviarMensagem(Long ticketId, String conteudo, String nomeRemetente) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        // Regra de Negócio: Se o ticket estiver FECHADO, impede novas mensagens
        if (ticket.getStatus() == Ticket.Status.FECHADO) {
            throw new RuntimeException("Não é possível enviar mensagens em um ticket fechado.");
        }

        // Regra de Negócio: Se for a primeira resposta, move para EM_ANDAMENTO
        if (ticket.getStatus() == Ticket.Status.ABERTO) {
            ticket.setStatus(Ticket.Status.EM_ANDAMENTO);
            ticket.setAtualizado(LocalDateTime.now());
            ticketRepository.save(ticket);
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setTicket(ticket);
        mensagem.setConteudo(conteudo);
        mensagem.setRemetenteNome(nomeRemetente);
        mensagem.setEnviadoEm(LocalDateTime.now());
        
        Mensagem salva = mensagemRepository.save(mensagem);
        
        return new MensagemResponseDTO(salva.getId(), salva.getConteudo(), salva.getRemetenteNome(), salva.getEnviadoEm());
    }

    public List<MensagemResponseDTO> listarMensagensDoTicket(Long ticketId) {
        return mensagemRepository.findByTicketIdOrderByEnviadoEmAsc(ticketId)
            .stream()
            .map(m -> new MensagemResponseDTO(m.getId(), m.getConteudo(), m.getRemetenteNome(), m.getEnviadoEm()))
            .collect(Collectors.toList());
    }


}