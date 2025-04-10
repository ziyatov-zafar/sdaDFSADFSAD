package uz.zafar.logisticsapplication.db.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.zafar.logisticsapplication.db.domain.Order;
import uz.zafar.logisticsapplication.db.repositories.OrderRepository;
import uz.zafar.logisticsapplication.db.service.OrderService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    @Autowired private  OrderRepository orderRepository;

    @Override
    public ResponseDto<Void> save(Order order) {
        try {
            orderRepository.save(order);
            return new ResponseDto<>(true , "Successfully saved order");
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<Order>> findAll() {
        try {
            List<Order> open = orderRepository.findAllByActiveAndStatusOrderByIdDesc(true, "open");
            return new ResponseDto<>(true , "Successfully find all orders" , open);
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<Order>> findAllByUserId(Long userId) {
        try {
            return new ResponseDto<>(true , "Ok" , orderRepository.findAllByUserIdAndActiveAndStatusOrderByIdAsc(
                    userId , true , "open"
            ));
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Order> findById(Long id) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                return new ResponseDto<>(true , "Ok" , order);
            }
            return new ResponseDto<>(false , "Not found" , null);
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage() );
        }
    }

    @Override
    public ResponseDto<Order> findByStatus(Long userId, String status) {
        try {
            Order order = orderRepository.findByUserIdAndStatus(userId, status);
            if (order != null)
            return new ResponseDto<>(true , "Ok" , order);
            return new ResponseDto<>(false , "Not found" , null);
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage() );
        }
    }
}
