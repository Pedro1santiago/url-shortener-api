package pedrosantiago.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pedrosantiago.model.URLShortener;

import java.util.Optional;

public interface URLRepository extends JpaRepository<URLShortener, Long> {

    Optional<URLShortener> findByUrlName(String urlName);
}
