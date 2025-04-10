package uz.zafar.logisticsapplication.db.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.repositories.UserRepository;
import uz.zafar.logisticsapplication.db.service.UserService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseDto<User> checkUser(Long chatId) {
        try {
            User user = userRepository.findByChatId(chatId);
            if (user == null) {
                throw new Exception("Not found user");
            }
            return new ResponseDto<>(true, "Ok", user);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Void> save(User user) {
        try {
            userRepository.save(user);
            return new ResponseDto<>(true, "Ok");
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }



    @Override
    public ResponseDto<User> findById(Long userId) {
        try {
            Optional<User> userOp = userRepository.findById(userId);
            if (userOp.isPresent()) {
                return new ResponseDto<>(true, "Ok", userOp.get());
            }
            throw new Exception("Not found user");
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<List<User>> findAll() {
        try {
            return new ResponseDto<>(true , "Ok" , userRepository.findAll(Sort.by("id")));
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }


    @Override
    public ResponseDto<Page<User>> findAllByUsername(String username, int page, int size) {
        try {
            Page<User> userPage = userRepository.findByUsernameContainingIgnoreCaseOrderByIdDesc(username, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            return new ResponseDto<>(false , e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAllByNickname(String nickname, int page, int size) {
        try {
            Page<User> userPage = userRepository.findByNicknameContainingIgnoreCaseOrderByIdDesc(nickname, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAllByRole(String role, int page, int size) {
        try {
            Page<User> userPage = userRepository.findAllByRoleOrderByIdAsc(role, PageRequest.of(page, size));
            return new ResponseDto<>(true, "Ok", userPage);
        } catch (Exception e) {
            return new ResponseDto<>(false, e.getMessage());
        }
    }

    @Override
    public ResponseDto<Page<User>> findAll(int page, int size) {
        try {
            Page<User> users = userRepository.findAllByOrderByIdDesc(PageRequest.of(page, size));
            return new ResponseDto<>(true , "Ok", users);
        } catch (Exception e) {
            return new ResponseDto<>(false  , e.getMessage());
        }
    }
}
