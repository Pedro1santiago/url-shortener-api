package pedrosantiago.EncurtaURL.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedrosantiago.EncurtaURL.model.EncurtadorUrl;

public interface UrlRepository extends JpaRepository<EncurtadorUrl, Long> {
}
