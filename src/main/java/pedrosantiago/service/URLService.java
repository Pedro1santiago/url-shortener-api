package pedrosantiago.service;

import org.springframework.stereotype.Service;
import pedrosantiago.dto.RequestDTO;
import pedrosantiago.dto.ResponseDTO;
import pedrosantiago.exception.URLAlreadyRegistered;
import pedrosantiago.exception.URLNameNullException;
import pedrosantiago.exception.URLValidationNotFalse;
import pedrosantiago.model.URLShortener;
import pedrosantiago.repository.URLRepository;

@Service
public class URLService {

    private final URLRepository urlRepository;

    public URLService(URLRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    public void verifyUrl(String url){

        int arroba = url.indexOf("@");
        int ponto = url.lastIndexOf(".");

        if (arroba == -1 || ponto == -1 || arroba > ponto || ponto == url.length() - 1) {
            throw new URLValidationNotFalse();
        }
    }

    public ResponseDTO createUrl(RequestDTO dto){

        if (dto.urlName() == null || dto.urlName().isBlank()){
            throw new URLNameNullException();
        }

        verifyUrl(dto.originalUrl());

        if (urlRepository.findByUrlName(dto.urlName()).isPresent()){
            throw new URLAlreadyRegistered();
        }

        URLShortener entity = new URLShortener();
        entity.setOriginUrl(dto.originalUrl());
        entity.setUrlName(dto.urlName());

        URLShortener saved = urlRepository.save(entity);

        return new ResponseDTO(saved.getUrlName());
    }
}