package disk.pasta.security;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

import java.util.Collection;


@Getter
public class CustomUserDetails extends User {
    private final Long id;
    private final String nome;

    public CustomUserDetails(Long id, String nome, String email, String senha, Collection<? extends GrantedAuthority> authorities) {
        super(email, senha, authorities);
        this.id = id;
        this.nome = nome;
    }

}