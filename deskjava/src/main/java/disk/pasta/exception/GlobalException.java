package disk.pasta.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArgumentExcepetion(IllegalArgumentException erro){
        return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(Map.of("menssage", erro.getMessage(), "sucesso", false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> MethodArgumentNotValidException(MethodArgumentNotValidException erro){
        return ResponseEntity
                .badRequest()
                .body(Map.of("mensagem", erro.getFieldErrors().get(0).getDefaultMessage(),"sucesso", false));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, 
    Object>> RuntimeException(RuntimeException erro){
        return ResponseEntity
        .badRequest()
        .body(Map.of("menssage", erro.getMessage(), "sucesso", false));
    }

}
