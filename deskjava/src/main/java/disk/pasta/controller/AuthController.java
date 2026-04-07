package disk.pasta.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import disk.pasta.dto.LoginRequestDTO;
import disk.pasta.dto.LoginResponseDTO;
import disk.pasta.service.LoginService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> efetuarLogin(@RequestBody @Valid LoginRequestDTO dados) {
        try {
            LoginResponseDTO response = loginService.logar(dados);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build(); // Não autorizado
        }
    }
}