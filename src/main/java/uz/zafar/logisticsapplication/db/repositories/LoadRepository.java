package uz.zafar.logisticsapplication.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.logisticsapplication.db.domain.Load;

import java.util.List;

public interface LoadRepository extends JpaRepository<Load, Long> {
    List<Load>findAllByStatusAndActiveAndUserIdOrderById(String status, Boolean active,Long userId);
    List<Load>findAllByStatusAndActiveOrderByIdDesc(String status, Boolean active);
    Load findByStatusAndUserId(String status,Long userId);
}

