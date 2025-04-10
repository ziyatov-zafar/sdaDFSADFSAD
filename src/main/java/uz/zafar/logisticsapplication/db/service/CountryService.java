package uz.zafar.logisticsapplication.db.service;

import org.springframework.data.domain.Page;
import uz.zafar.logisticsapplication.db.domain.Country;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

public interface CountryService {
    ResponseDto<Page<Country>>findAllByService(Long serviceId , int page , int size,String lang);
    ResponseDto<Country>findByStatus(String status);
    int countByServiceId(Long serviceId);

    ResponseDto<Void> save(Country country);

    ResponseDto<Country> findById(Long countryId);
}
