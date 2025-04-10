package uz.zafar.logisticsapplication.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.logisticsapplication.db.domain.Country;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country , Long> {
    List<Country>findAllByServiceIdAndStatusAndActiveOrderByNameUzAsc(Long serviceId , String status , Boolean active);
    List<Country>findAllByServiceIdAndStatusAndActiveOrderByNameRuAsc(Long serviceId , String status , Boolean active);

    Page<Country> findAllByServiceIdAndStatusAndActiveOrderByNameUzAsc(Long serviceId , String status , Boolean active , Pageable pageable);
    Page<Country>findAllByServiceIdAndStatusAndActiveOrderByNameRuAsc(Long serviceId , String status , Boolean active , Pageable pageable);
    Country findByStatus(String status);
}
