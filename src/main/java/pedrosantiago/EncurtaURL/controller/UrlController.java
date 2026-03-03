package pedrosantiago.EncurtaURL.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pedrosantiago.EncurtaURL.dto.RequestDTO;
import pedrosantiago.EncurtaURL.dto.ResponseDTO;
import pedrosantiago.EncurtaURL.service.UrlService;

@RestController
@RequestMapping("/encurtador")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

    //@PostMapping
    //public ResponseEntity<ResponseDTO> createUrl(RequestDTO dto){
    //    return ResponseEntity.ok(urlService.createUrl(dto));
    //}
}
