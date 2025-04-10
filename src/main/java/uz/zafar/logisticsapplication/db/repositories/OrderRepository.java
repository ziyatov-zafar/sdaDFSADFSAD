package uz.zafar.logisticsapplication.db.repositories;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.zafar.logisticsapplication.db.domain.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order>findAllByUserIdAndActiveAndStatusOrderByIdAsc(Long userId, Boolean active, String status);
    Order findByUserIdAndStatus(Long userId, String status);
    List<Order>findAllByActiveAndStatusOrderByIdDesc(Boolean active, String status);
}
