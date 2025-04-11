package uz.zafar.logisticsapplication.db.service.impl;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.zafar.logisticsapplication.db.domain.Country;
import uz.zafar.logisticsapplication.db.repositories.CountryRepository;
import uz.zafar.logisticsapplication.db.service.CountryService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.Optional;

@Service
//@Log4j2
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public ResponseDto<Page<Country>> findAllByService(Long serviceId, int page, int size, String lang) {
        try {

            Page<Country> countries;
            if (lang.equals("uz")) {
                countries = countryRepository.findAllByServiceIdAndStatusAndActiveOrderByNameUzAsc(serviceId, "open", true, PageRequest.of(page, size));
            } else {
                countries = countryRepository.findAllByServiceIdAndStatusAndActiveOrderByNameRuAsc(serviceId, "open", true, PageRequest.of(page, size));
            }
            return new ResponseDto<>(true, "Ok", countries);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getLocalizedMessage());
        }
    }


    @Override
    public ResponseDto<Country> findById(Long countryId) {
        try {
            Optional<Country> check = countryRepository.findById(countryId);
            return check.map(country -> new ResponseDto<>(true, "Ok", country)).orElseGet(() -> new ResponseDto<>(false, "Not found country"));
        } catch (Exception e) {

            return new ResponseDto<>(false , e.getLocalizedMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(Country country) {
        try {
            countryRepository.save(country);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {

            return new ResponseDto<>(false, e.getLocalizedMessage());
        }
    }

    @Override
    public int countByServiceId(Long serviceId) {
        try {
            return countryRepository.findAllByServiceIdAndStatusAndActiveOrderByNameUzAsc(serviceId, "open", true).size();
        } catch (Exception e) {

            return -1;
        }
    }

    @Override
    public ResponseDto<Country> findByStatus(String status) {
        try {
            Country country = countryRepository.findByStatus(status);
            if (country != null)
                return new ResponseDto<>(true, "Ok", country);
            return new ResponseDto<>(false, "Not found");
        } catch (Exception e) {

            return new ResponseDto<>(false, e.getLocalizedMessage());
        }
    }
}
