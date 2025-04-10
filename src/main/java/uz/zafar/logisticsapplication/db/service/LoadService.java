package uz.zafar.logisticsapplication.db.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.zafar.logisticsapplication.db.domain.Load;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;


public interface LoadService {
    ResponseDto<List<Load>>findAll(Long userId);
    ResponseDto<Void>deleteById(Long loadId);
    ResponseDto<Void>save(Load load);
    ResponseDto<Load>findById(Long loadId);
    ResponseDto<Load>findByStatus(String status,Long userId);

    ResponseDto<List<Load>> findAllForUser();
}
