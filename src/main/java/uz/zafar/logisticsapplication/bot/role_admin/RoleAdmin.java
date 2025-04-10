package uz.zafar.logisticsapplication.bot.role_admin;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.zafar.logisticsapplication.db.domain.User;

import static uz.zafar.logisticsapplication.bot.StaticVariables.backButton;


@Controller
public class RoleAdmin {
    private final AdminFunction function;

    public RoleAdmin(AdminFunction function) {
        this.function = function;
    }

    public void menu(User user, Update update, int size) {
        String eventCode = user.getEventCode();

        if (update.hasMessage()) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (eventCode.equals("reklama")) {
                    if (message.hasText()) {
                        String text = message.getText();
                        if (text.equals("/start") || text.equals(backButton)) {
                            function.start(user);
                            return;
                        }
                    }
                    function.reklama(user, message.getMessageId());
                }
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start")) {
                        function.start(user);
                    } else {
                        switch (eventCode) {
                            case "crud service" -> function.crudService(user, null, null, text);
                            case "menu" -> function.menu(user, text);
                            case "users page" -> function.usersPage(user, text, size);
                            case "get chat id" -> function.getChatId(user, text);
                            case "get username" -> function.getUsername(user, text);
                            case "get nickname" -> function.getNickname(user, text);
                            case "get id" -> function.getId(user, text);
                            case "is send reklama" -> function.isSendReklama(user, text);
                            case "channel crud" -> function.channelCrud(user, text);
                            case "add new channel name" -> function.addNewChannelName(user, text);
                            case "add new channel username" -> function.addNewChannelUsername(user, text);
                            case "add new channel link" -> function.addNewChannelLink(user, text);
                            case "is add new channel" -> function.isAddNewChannel(user, text);
                            case "edit channel name", "edit channel id", "edit channel link" ->
                                    function.editChannel(user, text, eventCode);
                            case "phone menu" -> function.phoneMenu(user, text);
                            case "get new service name" -> function.getNewServiceName(user, text);
                            case "get new service name ru" -> function.getNewServiceNameRu(user, text);

                            case "edit name uz","edit name ru" -> function.editService(user , text , eventCode);
                            case "add country uz" , "add country ru" -> function.addCountry(user , text ,eventCode);

                        }
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            Integer messageId = callbackQuery.getMessage().getMessageId();
            switch (eventCode) {
                case "menu" -> function.menu(user, data, messageId, callbackQuery);
                case "get all users" -> function.getAllUsers(user, data, messageId, callbackQuery, size);
                case "get chat id" -> function.getChatId(user, data, messageId, callbackQuery, size);
                case "get username" -> function.getUsername(user, data, callbackQuery, messageId);
                case "get nickname" -> function.getNickname(user, data, callbackQuery, messageId);
                case "get id" -> function.getId(user, data, callbackQuery, messageId);
                case "get all drivers" -> function.getAllUserBlock(user, data, callbackQuery, messageId, "driver");
                case "get all user active" ->
                        function.getAllUserBlock(user, data, callbackQuery, messageId, "user_active");
                case "get all block" -> function.getAllUserBlock(user, data, callbackQuery, messageId, "block");
                case "channel crud" -> function.channelCrud(user, data, callbackQuery, messageId);
                case "get all loaders" -> function.getAllLoaders(user, data, callbackQuery, messageId, "loader");
                case "crud service" -> function.crudService(user, callbackQuery, messageId, data);
            }
        }
    }
}
