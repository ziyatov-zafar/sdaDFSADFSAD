package uz.zafar.logisticsapplication.bot.role_loader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.logisticsapplication.db.domain.User;

@Controller
public class RoleLoader {
    private final LoaderFunction function;

    public RoleLoader(LoaderFunction function) {
        this.function = function;
    }

    public void mainMenu(Update update, User user) {
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
                    } else if (eventCode.equals("get full name") || eventCode.equals("get address") ||
                            eventCode.equals("get load name") || eventCode.equals("get load weight") || eventCode.equals("get load price") ||
                            eventCode.equals("get load car count") || eventCode.equals("get load is advance") || eventCode.equals("get load advance price") ||
                            eventCode.equals("get load payment type") || eventCode.equals("get load phone") || eventCode.equals("is add load")
                    ) {
                        function.add(user, text);
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
                if (eventCode.equals("menu")) {
                    function.menu(user, data, callbackQuery, messageId);
                }
            }
        }
    }
}
