package uz.zafar.logisticsapplication.bot.role_super_admin;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.logisticsapplication.db.domain.User;


@Controller
//@Log4j2
public class SuperAdminRole {
    private final SuperAdminFunction function;

    public SuperAdminRole(SuperAdminFunction function) {
        this.function = function;
    }

    public void mainMenu(User user, Update update, int size) {
        String eventCode = user.getEventCode();
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (eventCode.equals("reklama")) {
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start") || text.equals("\uD83D\uDD19 Orqaga qaytish")) {
                        function.start(user);
                        return;
                    } else {
                        function.reklama(user, user.getChatId(), message.getMessageId());
                    }
                }else{
                    function.reklama(user, user.getChatId(), message.getMessageId());
                }
            }
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    function.start(user);
                } else {
                    if (eventCode.equals("menu")) {
                        function.menu(user, text, size);
                    } else if (eventCode.equals("users page")) {
                        function.usersPage(user.getChatId(), message.getMessageId());
                    } else if (eventCode.equals("get chat id")) {
                        function.getChatId(user , text);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            if (eventCode.equals("users page")) {
                function.usersPage(user, data, messageId, callbackQuery, size);
            }else if (eventCode.equals("get chat id")) {
                function.getChatId(user , data , callbackQuery , callbackQuery.getMessage().getMessageId(),size);
            }
        }
    }
}
