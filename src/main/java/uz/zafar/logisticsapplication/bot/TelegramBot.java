package uz.zafar.logisticsapplication.bot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.zafar.logisticsapplication.bot.role_admin.AdminFunction;
import uz.zafar.logisticsapplication.bot.role_admin.AdminKyb;
import uz.zafar.logisticsapplication.bot.role_admin.RoleAdmin;
import uz.zafar.logisticsapplication.bot.role_loader.LoaderFunction;
import uz.zafar.logisticsapplication.bot.role_loader.LoaderKyb;
import uz.zafar.logisticsapplication.bot.role_loader.RoleLoader;
import uz.zafar.logisticsapplication.bot.role_loader.RoleLoaderMsg;
import uz.zafar.logisticsapplication.bot.role_super_admin.SuperAdminFunction;
import uz.zafar.logisticsapplication.bot.role_super_admin.SuperAdminKyb;
import uz.zafar.logisticsapplication.bot.role_super_admin.SuperAdminRole;
import uz.zafar.logisticsapplication.bot.role_user_active.RoleUserActive;
import uz.zafar.logisticsapplication.bot.role_user_active.UserActiveFunction;
import uz.zafar.logisticsapplication.bot.role_user_active.UserActiveKyb;
import uz.zafar.logisticsapplication.bot.role_driver.RoleDriver;
import uz.zafar.logisticsapplication.bot.role_driver.RoleDriverMsg;
import uz.zafar.logisticsapplication.bot.role_driver.DriverFunction;
import uz.zafar.logisticsapplication.bot.role_driver.DriverKyb;
import uz.zafar.logisticsapplication.bot.role_user_active.UserActiveMsg;
import uz.zafar.logisticsapplication.db.domain.Channel;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.repositories.LoadRepository;
import uz.zafar.logisticsapplication.db.service.*;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private LoaderKyb loaderKyb;
    @Autowired
    private UserService userService;
    @Autowired
    private ChannelService channelService;

    @Value("${bot.token}")
    public String botToken;
    @Value("${size}")
    public int size;
    @Value("${admin.chat.id}")
    private Long superAdminChatId;


    @Value("${bot.username}")
    public String botUsername;
    @Autowired
    private SuperAdminKyb superAdminKyb;
    @Autowired
    private UserActiveKyb userKyb;
    @Autowired
    private AdminKyb adminKyb;
    @Autowired
    private DriverKyb driverKyb;
    @Autowired
    private RoleDriverMsg roleDriverMsg;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private RoleLoaderMsg roleLoaderMsg;
    @Autowired
    private UserActiveMsg userActiveMsg;
    @Autowired
    private LoadService loadService;
    @Autowired
    private LoadRepository loadRepository;
    @Autowired
    private CountryService countryService;
    @Autowired
    private OrderService orderService;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        Long chatId;
        String username, firstname, lastname, nickname;
        if (update.hasMessage()) {
            username = update.getMessage().getFrom().getUserName();
            firstname = update.getMessage().getFrom().getFirstName();
            lastname = update.getMessage().getFrom().getLastName();
            nickname = firstname + (lastname == null ? "" : " " + lastname);
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            username = update.getCallbackQuery().getFrom().getUserName();
            firstname = update.getCallbackQuery().getFrom().getFirstName();
            lastname = update.getCallbackQuery().getFrom().getLastName();
            nickname = firstname + (lastname == null ? "" : " " + lastname);
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            return;
        }
        ResponseDto<User> checkUser = userService.checkUser(chatId);
        User user;
        if (checkUser.isSuccess())
            user = checkUser.getData();
        else {
            user = new User();
            user.setChatId(chatId);
            user.setRole("user_active");
            user.setEventCode("");
            user.setPage(0);
            userService.save(user);
            checkUser = userService.checkUser(chatId);
            user = checkUser.getData();
        }
        user.setUsername(username);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setNickname(nickname);
        userService.save(user);
        if (user.getChatId().equals(superAdminChatId)) {
            if (!user.getRole().equals("super_admin")) {
                user.setRole("super_admin");
                userService.save(user);
            }
            new SuperAdminRole(
                    new SuperAdminFunction(
                            this, userService, superAdminKyb
                    )
            ).mainMenu(user, update, size);
        } else {
            switch (user.getRole()) {
                case "user_active" -> new RoleUserActive(
                        new UserActiveFunction(
                                this, userService,
                                userKyb, userActiveMsg
                        )
                ).menu(user, update, size);
                case "driver" -> new RoleDriver(
                        new DriverFunction(
                                this, userService,
                                driverKyb, roleDriverMsg,
                                serviceService, countryService,
                                orderService
                        )
                ).menu(user, update, size);
                case "admin" -> new RoleAdmin(
                        new AdminFunction(this, userService, adminKyb,
                                channelService, loadService, serviceService,
                                countryService, orderService)
                ).menu(user, update, size);
                case "block" -> {
                    int messageId;
                    if (update.hasMessage()) {
                        messageId = update.getMessage().getMessageId();
                        deleteMessage(chatId, messageId);
                        sendMessage(chatId, "Kechirasiz, bu bot siz uchun ishlamaydi chunki admin tomonidan bloklangansiz", true);
                    } else {
                        alertMessage(update.getCallbackQuery(), "Kechirasiz, bu bot siz uchun ishlamaydi chunki admin tomonidan bloklangansiz");
                        messageId = update.getCallbackQuery().getMessage().getMessageId();
                        deleteMessage(chatId, messageId);
                    }
                }
                case "loader" -> {
                    new RoleLoader(
                            new LoaderFunction(
                                    userService, this, loaderKyb, roleLoaderMsg,
                                    loadService, loadRepository
                            )
                    ).mainMenu(update, user);
                }
            }
        }
    }


    public String getChatMember(String chatId, long userId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.telegram.org/bot" + botToken + "/getChatMember" + "?chat_id=" + chatId + "&user_id=" + userId;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            if (rootNode.path("ok").asBoolean()) {
                return rootNode.path("result").path("status").asText();
            } else {
                return "error";
            }
        } catch (JsonProcessingException e) {
            sendMessage(userId, e.getMessage());
        }
        return "error";
    }

    public List<Channel> getChannels(long chatId) {
        List<Channel> list = channelService.findAll().getData();
        List<Channel> channels = new ArrayList<>(list.size());
        String[] types = {"member", "creator", "administrator"};
        for (Channel channel : list) {
            if (!inArray(getChatMember(channel.getChannelId(), chatId), types)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    private boolean inArray(String s, String[] a) {
        for (String string : a) {
            if (string.equals(s)) return true;
        }
        return false;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }


    @SneakyThrows
    public void sendMessage(Long chatId, String text) {
        try {
            execute(
                    SendMessage
                            .builder()
                            .chatId(chatId)
                            .text(text)
                            .parseMode(ParseMode.HTML)
                            .disableWebPagePreview(true)
                            .build()
            );

        } catch (TelegramApiException e) {

        }
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup) {
        try {
            execute(
                    SendMessage
                            .builder()
                            .chatId(chatId)
                            .text(text)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(markup)
                            .disableWebPagePreview(true)
                            .build()
            );
        } catch (TelegramApiException e) {

        }
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        try {
            execute(
                    SendMessage
                            .builder()
                            .chatId(chatId)
                            .text(text)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(markup)
                            .disableWebPagePreview(true)
                            .build()
            );

        } catch (TelegramApiException e) {

        }
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, Boolean remove) {
        try {
            execute(
                    SendMessage
                            .builder()
                            .chatId(chatId)
                            .text(text)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(new ReplyKeyboardRemove(remove))
                            .disableWebPagePreview(true)
                            .build()
            );

        } catch (TelegramApiException e) {

        }
    }

    public void alertMessage(CallbackQuery callbackQuery, String alertMessageText) {

        String callbackQueryId = callbackQuery.getId();
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setText(alertMessageText);
        answerCallbackQuery.setCallbackQueryId(callbackQueryId);

        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {

        }
    }

    public void sendVideo(Long chatId, String fileId, String caption, InlineKeyboardMarkup markup, boolean isAdmin) {
        try {
            execute(
                    SendVideo.builder()
                            .video(new InputFile(fileId))
                            .chatId(chatId)
                            .protectContent(!isAdmin)
                            .caption(caption)
                            .parseMode("HTML")
                            .replyMarkup(markup)
                            .build()
            );
        } catch (TelegramApiException e) {

        }
    }

    public void deleteMessage(Long chatId, int messageId) {
        try {
            execute(DeleteMessage.builder().messageId(messageId).chatId(chatId).build());
        } catch (TelegramApiException e) {

        }
    }

    public void editMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) {
        try {
            execute(EditMessageText.builder().chatId(chatId).messageId(messageId).text(text).replyMarkup(markup).parseMode("HTML").build());
        } catch (TelegramApiException e) {
        }
    }

    public void editMessageText(Long chatId, Integer messageId, String text) {
        try {
            execute(EditMessageText.builder().chatId(chatId).messageId(messageId).text(text).parseMode("HTML").build());
        } catch (TelegramApiException e) {

        }
    }
}
