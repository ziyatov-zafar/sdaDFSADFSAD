package uz.zafar.logisticsapplication.bot.role_user_active;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.logisticsapplication.db.domain.User;


@Controller
public class RoleUserActive {
    private final UserActiveFunction function;

    public RoleUserActive(UserActiveFunction function) {
        this.function = function;
    }

    public void menu(User user, Update update, int size) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (function.isFinished(user, function.bot)) {
                    function.updateChannel(user, function.bot, function.kyb);
                    return;
                }
                if (text.equals("/start")) {
                    function.start(user);
                } else {
                    switch (eventCode) {
                        case "request lang" -> function.requestLang(user, text, message.getMessageId());
                        case "menu" -> function.menu(user, text);
                        case "choose role" -> function.chooseRole(user, text);
                    }
                }
            } else if (message.hasContact()) {
                if (eventCode.equals("request contact")) {
                    function.requestContact(user, message.getContact());
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("success")) {
                if (function.success(callbackQuery,user, function.bot , function.kyb)) {
                    function.start(user);
                }
            } else {

            }
        }
    }
}
