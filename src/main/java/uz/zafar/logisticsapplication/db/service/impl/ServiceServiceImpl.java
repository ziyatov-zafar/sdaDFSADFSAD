package uz.zafar.logisticsapplication.db.service.impl;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uz.zafar.logisticsapplication.db.repositories.ServiceRepository;
import uz.zafar.logisticsapplication.db.service.ServiceService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
//@Log4j2
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public ResponseDto<List<uz.zafar.logisticsapplication.db.domain.Service>> findAll(String lang) {
        try {
            if (lang.equals("uz"))
                return new ResponseDto<>(true, "Ok", serviceRepository.findAllByStatusAndActiveOrderByNameUzAsc("open", true));
            return new ResponseDto<>(true, "Ok", serviceRepository.findAllByStatusAndActiveOrderByNameRuAsc("open", true));
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<uz.zafar.logisticsapplication.db.domain.Service> findByNameUz(String nameUz) {
        try {
            uz.zafar.logisticsapplication.db.domain.Service byNameUz = serviceRepository.findByNameUz(nameUz);
            if (byNameUz == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", byNameUz);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }

    }

    @Override
    public ResponseDto<uz.zafar.logisticsapplication.db.domain.Service> findByNameRu(String nameRu) {
        try {
            uz.zafar.logisticsapplication.db.domain.Service byNameUz = serviceRepository.findByNameRu(nameRu);
            if (byNameUz == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", byNameUz);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }

    }

    @Override
    public ResponseDto<uz.zafar.logisticsapplication.db.domain.Service> findById(Long id) {
        try {
            Optional<uz.zafar.logisticsapplication.db.domain.Service> serviceDto = serviceRepository.findById(id);
            return serviceDto.map(service -> new ResponseDto<>(true, "Ok", service)).orElseGet(() -> new ResponseDto<>(false, "Not found"));
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(uz.zafar.logisticsapplication.db.domain.Service service) {
        try {
            serviceRepository.save(service);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<uz.zafar.logisticsapplication.db.domain.Service> findByStatus(String status) {
        try {
            uz.zafar.logisticsapplication.db.domain.Service byNameUz = serviceRepository.findByStatus(status);
            if (byNameUz == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", byNameUz);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }

    }
}
