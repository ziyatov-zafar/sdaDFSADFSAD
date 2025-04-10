package uz.zafar.logisticsapplication.db.service;

import uz.zafar.logisticsapplication.db.domain.Order;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

public interface OrderService {
    ResponseDto<Void>save(Order order);
    ResponseDto<List<Order>>findAllByUserId(Long userId);
    ResponseDto<List<Order>>findAll();
    ResponseDto<Order>findById(Long id);
    ResponseDto<Order>findByStatus(Long userId,String status);
}
