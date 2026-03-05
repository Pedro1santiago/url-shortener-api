package urlshortener.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;

@Entity
@Table(name = "shortener")
public class UrlShortener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;

    @Column(unique = true)
    private String urlName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
    public void setOriginUrl(String originalUrl) {this.originalUrl = originalUrl;}

    public String getUrlName() {
        return urlName;
    }
    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }
}
