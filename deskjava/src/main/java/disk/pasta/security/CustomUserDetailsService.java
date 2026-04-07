package disk.pasta.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import disk.pasta.model.Cliente;
import disk.pasta.model.Tecnico;
import disk.pasta.repository.ClienteRepository;
import disk.pasta.repository.TecnicoRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Tenta buscar o técnico
        var tecnicoOpt = tecnicoRepository.findByEmail(email);
        if (tecnicoOpt.isPresent()) {
            Tecnico tecnico = tecnicoOpt.get();
            // Criamos a autoridade com o prefixo ROLE_ (necessário para hasRole)
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + tecnico.getNivel().name().toUpperCase()));
            
            return new CustomUserDetails(
                tecnico.getId(), 
                tecnico.getNome(),
                tecnico.getEmail(), 
                tecnico.getSenha(), 
                authorities
            );
        }

        // 2. Tenta buscar o cliente
        var clienteOpt = clienteRepository.findByEmail(email);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
            
            return new CustomUserDetails(
                cliente.getId(), 
                cliente.getNome(),
                cliente.getEmail(), 
                cliente.getSenha(), 
                authorities
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado: " + email);
    }
}