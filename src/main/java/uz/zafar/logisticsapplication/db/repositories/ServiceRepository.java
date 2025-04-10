package uz.zafar.logisticsapplication.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.logisticsapplication.db.domain.Service;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service , Long> {
    List<Service> findAllByStatusAndActiveOrderByNameUzAsc(String status, Boolean active);
    List<Service> findAllByStatusAndActiveOrderByNameRuAsc(String status, Boolean active);
    Service findByNameUz(String nameUz);
    Service findByNameRu(String nameRu);
    Service findByStatus(String status);
}
