package pedrosantiago.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pedrosantiago.dto.RequestDTO;
import pedrosantiago.dto.ResponseDTO;
import pedrosantiago.service.URLService;

@RestController
@RequestMapping("/shortener")
public class URLController {

    private final URLService urlService;

    public URLController(URLService urlService){
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createUrl(@RequestBody RequestDTO dto){
        return ResponseEntity.ok(urlService.createUrl(dto));
    }
}
