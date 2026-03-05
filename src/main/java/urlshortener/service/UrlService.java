package urlshortener.service;

import org.springframework.stereotype.Service;
import urlshortener.dto.RequestDTO;
import urlshortener.dto.ResponseDTO;
import urlshortener.exception.UrlAlreadyRegistered;
import urlshortener.exception.UrlNameNullException;
import urlshortener.exception.UrlNotRegisteredException;
import urlshortener.exception.UrlValidationNotFalse;
import urlshortener.model.UrlShortener;
import urlshortener.repository.UrlRepository;
import urlshortener.util.ShortCodeGenerator;

import java.util.Optional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }

    public void verifyUrl(String url){

        if(url == null || url.isBlank()){
            throw new UrlValidationNotFalse();
        }

        int protocolHttp = url.indexOf("http://");
        int protocolHttps = url.indexOf("https://");
        int ponto = url.lastIndexOf(".");

        if ((protocolHttp == -1 && protocolHttps == -1)
                || ponto == -1
                || ponto == url.length() - 1) {

            throw new UrlValidationNotFalse();
        }
    }

    private String generateUniqueCode() {

        String code;

        do {
            code = ShortCodeGenerator.generateCode(5);
        } while (urlRepository.findByUrlName(code).isPresent());

        return code;
    }

    public ResponseDTO randomUrl(RequestDTO dto) {

        verifyUrl(dto.originalUrl());

        Optional<UrlShortener> existing =
                urlRepository.findByOriginalUrl(dto.originalUrl());

        UrlShortener url = new UrlShortener();

        if (existing.isPresent()) {
            url.setOriginUrl(existing.get().getOriginalUrl());
        } else {
            url.setOriginUrl(dto.originalUrl());
        }

        url.setUrlName(generateUniqueCode());

        UrlShortener saved = urlRepository.save(url);

        return new ResponseDTO(saved.getUrlName());
    }

    public ResponseDTO createUrl(RequestDTO dto){

        if (dto.urlName() == null || dto.urlName().isBlank()){
            throw new UrlNameNullException();
        }

        verifyUrl(dto.originalUrl());

        if (urlRepository.findByUrlName(dto.urlName()).isPresent()){
            throw new UrlAlreadyRegistered();
        }

        UrlShortener entity = new UrlShortener();
        entity.setOriginUrl(dto.originalUrl());
        entity.setUrlName(dto.urlName());

        UrlShortener saved = urlRepository.save(entity);

        return new ResponseDTO(saved.getUrlName());
    }

    public String getOriginUrl(String urlName){

        UrlShortener url = urlRepository
                .findByUrlName(urlName)
                .orElseThrow(UrlNotRegisteredException::new);

        return url.getOriginalUrl();

    }



}