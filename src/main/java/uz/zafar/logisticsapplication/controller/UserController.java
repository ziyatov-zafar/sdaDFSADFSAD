package uz.zafar.logisticsapplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.service.UserService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Object a() {
        return userService.findAll();
    }
}
