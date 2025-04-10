package uz.zafar.logisticsapplication.bot.role_driver;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.logisticsapplication.db.domain.User;

@Controller
@Log4j2
public class RoleDriver {
    private final DriverFunction function;

    public RoleDriver(DriverFunction function) {
        this.function = function;
    }

    public void menu(User user, Update update, int size) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (function.isFinished(user, function.bot)) {
                function.updateChannel(user, function.bot, function.kyb);
                return;
            }
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    function.start(user);
                } else {
                    if (eventCode.equals("menu")) {
                        function.menu(user, text);
                    } else if (eventCode.equals("countries menu")) {
                        function.countriesMenu(user, text);
                    } else if (function.inArray(eventCode, "get full name" , "get phone number")) {
                        function.addOrder(user , text , eventCode);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            if (data.equals("success")) {
                if (function.success(callbackQuery, user, function.bot, function.kyb)) {
                    function.start(user);
                }
            } else {
                if (eventCode.equals("countries menu")) {
                    function.countriesMenu(user, messageId, callbackQuery, data);
                } else if (eventCode.equals("menu")) {
                    function.menu(user,callbackQuery , messageId , data );
                }
            }
        }
    }
}
