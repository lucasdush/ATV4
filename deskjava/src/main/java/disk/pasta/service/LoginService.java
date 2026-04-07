package disk.pasta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import disk.pasta.dto.LoginRequestDTO;
import disk.pasta.dto.LoginResponseDTO;
import disk.pasta.model.Cliente;
import disk.pasta.model.Tecnico;
import disk.pasta.repository.ClienteRepository;
import disk.pasta.repository.TecnicoRepository;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDTO logar(LoginRequestDTO dados) {
        System.out.println("DEBUG: Tentativa de login para: " + dados.getEmail());
        
        // 1. Tentar buscar em Clientes
        Optional<Cliente> cliente = clienteRepository.findByEmail(dados.getEmail());
        if (cliente.isPresent()) {
            System.out.println("DEBUG: Cliente encontrado. Verificando senha...");
            if (passwordEncoder.matches(dados.getSenha(), cliente.get().getSenha())) {
                return new LoginResponseDTO(cliente.get().getId(), "token-fake-123", cliente.get().getNome(), "CLIENTE");
            }
        }

        // 2. Tentar buscar em Técnicos
        Optional<Tecnico> tecnico = tecnicoRepository.findByEmail(dados.getEmail());
        if (tecnico.isPresent()) {
            System.out.println("DEBUG: Técnico encontrado. Verificando senha...");
            if (passwordEncoder.matches(dados.getSenha(), tecnico.get().getSenha())) {
                // Verifica se o técnico está ativo (conforme definido no seu Tecnico.java)
                if (!tecnico.get().isAtivo()) {
                    System.out.println("DEBUG: Técnico inativo.");
                    throw new RuntimeException("Acesso negado: Técnico inativo.");
                }
                return new LoginResponseDTO(tecnico.get().getId(), "token-fake-456", tecnico.get().getNome(), "TECNICO");
            } else {
                System.out.println("DEBUG: Senha incorreta para Técnico.");
            }
        }

        System.out.println("DEBUG: Login falhou para: " + dados.getEmail());
        throw new RuntimeException("E-mail ou senha inválidos.");
    }
}