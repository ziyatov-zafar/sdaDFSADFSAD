package uz.zafar.logisticsapplication.db.service;

import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.zafar.logisticsapplication.db.domain.Service;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

public interface ServiceService {
    ResponseDto<List<Service>>findAll(String lang);
    ResponseDto<Service>findByNameUz(String nameUz);
    ResponseDto<Service>findByNameRu(String nameRu);
    ResponseDto<Service>findByStatus(String status);
    ResponseDto<Void>save(Service service);

    ResponseDto<Service> findById(Long id);
}
