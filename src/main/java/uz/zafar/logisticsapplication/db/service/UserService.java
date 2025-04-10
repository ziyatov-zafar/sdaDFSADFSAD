package uz.zafar.logisticsapplication.db.service;

import org.springframework.data.domain.Page;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

public interface UserService {
    ResponseDto<User> checkUser(Long chatId);

    ResponseDto<User> findById(Long userId);

    ResponseDto<Void> save(User user);

    ResponseDto<Page<User>> findAll(int page, int size);

    ResponseDto<List<User>> findAll();

    ResponseDto<Page<User>> findAllByUsername(String username, int page, int size);

    ResponseDto<Page<User>> findAllByNickname(String nickname, int page, int size);

    ResponseDto<Page<User>> findAllByRole(String role, int page, int size);
}
