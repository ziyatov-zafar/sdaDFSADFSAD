package uz.zafar.logisticsapplication.db.service.impl;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.zafar.logisticsapplication.db.domain.Load;
import uz.zafar.logisticsapplication.db.repositories.LoadRepository;
import uz.zafar.logisticsapplication.db.service.LoadService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
//@Log4j2
public class LoadServiceImpl implements LoadService {
    @Autowired
    private LoadRepository loadRepository;

    @Override
    public ResponseDto<List<Load>> findAllForUser() {
        try {
            List<Load> loads = loadRepository.findAllByStatusAndActiveOrderByIdDesc("open", true);
            return new ResponseDto<>(true, "Ok", loads);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto<List<Load>> findAll(Long userId) {
        try {
            List<Load> loads = loadRepository.findAllByStatusAndActiveAndUserIdOrderById("open", true, userId);
            return new ResponseDto<>(true, "Ok", loads);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto<Void> deleteById(Long loadId) {
        try {
            Optional<Load> lOp = loadRepository.findById(loadId);
            if (lOp.isPresent()) {
                Load load = lOp.get();
                load.setActive(Boolean.FALSE);
                loadRepository.save(load);
                return new ResponseDto<>(true, "Ok", null);
            }
            return new ResponseDto<>(false, "Not found", null);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto<Void> save(Load load) {
        try {
            Load save = loadRepository.save(load);
            return new ResponseDto<>(true, "Ok", null);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto<Load> findById(Long loadId) {
        try {
            Optional<Load> lOp = loadRepository.findById(loadId);
            return lOp.map(load -> new ResponseDto<>(true, "Ok", load)).orElseGet(() -> new ResponseDto<>(false, "Not found", null));
        } catch (Exception e) {

            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }

    @Override
    public ResponseDto<Load> findByStatus(String status,Long userId) {
        try {
            Load load = loadRepository.findByStatusAndUserId(status,userId);
            if (load == null) {
                return new ResponseDto<>(false, "Not found");
            }
            return new ResponseDto<>(true, "Ok", load);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage(), null);
        }
    }
}
