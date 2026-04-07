package disk.pasta.service;

import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import disk.pasta.dto.ClienteRequestDTO;
import disk.pasta.dto.ClienteResponseDTO;
import disk.pasta.model.Cliente;
import disk.pasta.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    
    public Page<ClienteResponseDTO> ListarTodos(Pageable pageable){
        return clienteRepository
        .findAll(pageable)
        .map(u -> new ClienteResponseDTO(u.getId(), u.getNome(), u.getEmail()));
    }

    // Salvar um novo cliente. 
    public Cliente salvarCliente(ClienteRequestDTO clienteRequestDTO){
        if(clienteRepository.findByEmail(clienteRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("email já cadastrado");
        }

        
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(clienteRequestDTO.getNome());
        novoCliente.setEmail(clienteRequestDTO  .getEmail());
        novoCliente.setCpf(clienteRequestDTO.getCpf());
        novoCliente.setTelefone(clienteRequestDTO.getTelefone());
        // Criptografar a senha antes de salvar
        novoCliente.setSenha(bCryptPasswordEncoder.encode(clienteRequestDTO.getSenha()));

        return clienteRepository.save(novoCliente);
        
    }

    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            cliente.setNome(dto.getNome());
        }
        
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            cliente.setSenha(bCryptPasswordEncoder.encode(dto.getSenha()));
        }

        Cliente updated = clienteRepository.save(cliente);
        return new ClienteResponseDTO(updated.getId(), updated.getNome(), updated.getEmail());
    }

    public void deletarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        clienteRepository.delete(cliente);
    }
}
    



