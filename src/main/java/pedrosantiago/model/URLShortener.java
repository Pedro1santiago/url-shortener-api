package pedrosantiago.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;

@Entity
@Table(name = "shortener")
public class URLShortener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalURL;

    @Column(unique = true)
    private String urlName;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalURL() {
        return originalURL;
    }
    public void setOriginUrl(String originUrl) {this.originalURL = originalURL;}

    public String getUrlName() {
        return urlName;
    }
    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }
}
