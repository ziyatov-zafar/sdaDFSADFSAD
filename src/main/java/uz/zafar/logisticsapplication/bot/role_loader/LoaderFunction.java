package uz.zafar.logisticsapplication.bot.role_loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.zafar.logisticsapplication.bot.Function;
import uz.zafar.logisticsapplication.bot.TelegramBot;
import uz.zafar.logisticsapplication.db.domain.Load;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.repositories.LoadRepository;
import uz.zafar.logisticsapplication.db.service.LoadService;
import uz.zafar.logisticsapplication.db.service.UserService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

@Controller
@Log4j2
public class LoaderFunction extends Function {
    private final UserService userService;
    public final TelegramBot bot;
    public final LoaderKyb kyb;
    private final RoleLoaderMsg msg;
    private final LoadService loadService;
    private final LoadRepository loadRepository;

    public LoaderFunction(UserService userService, TelegramBot bot, LoaderKyb kyb, RoleLoaderMsg msg, LoadService loadService, LoadRepository loadRepository) {
        this.userService = userService;
        this.bot = bot;
        this.kyb = kyb;
        this.msg = msg;
        this.loadService = loadService;
        this.loadRepository = loadRepository;
    }

    public void start(User user) {
        bot.sendMessage(user.getChatId(), msg.menu(user.getLang()), kyb.menu(user.getLang()));
        user.setEventCode("menu");
        userService.save(user);

    }

    public void menu(User user, String text) {
        if (inArray(text, arr("➕ Yuk qo‘shish", "➕ Добавить груз"))) {
            bot.sendMessage(user.getChatId(), msg.addLoad(user.getLang()), kyb.back(user.getLang()));
            user.setEventCode("get full name");
            userService.save(user);
        } else if (inArray(text, arr("📦 Mening yuklarim", "📦 Мои грузы"))) {
            ResponseDto<List<Load>> all = loadService.findAll(user.getId());
            if (all.isSuccess()) {
                if (all.getData().isEmpty()) {
                    bot.sendMessage(user.getChatId(), msg.notFoundLoad(user.getLang()), kyb.back(user.getLang()));
                    return;
                }
                bot.sendMessage(user.getChatId(), text, kyb.back(user.getLang()));
                for (Load load : all.getData()) {
                    bot.sendMessage(user.getChatId(), msg.loadInformation(user.getLang(), load), kyb.deleteBtn(user.getLang(), load.getId()));
                }
            } else {
                bot.sendMessage(user.getChatId(), msg.notFoundLoad(user.getLang()), kyb.back(user.getLang()));
            }
        } else if (inArray(text, arr("⚙️ Изменить язык", "⚙️ Tilni o‘zgartirish"))) {
            user.setLang(user.getLang().equals("uz") ? "ru" : "uz");
            bot.sendMessage(user.getChatId(), msg.changeLang(user.getLang()), kyb.menu(user.getLang()));

            userService.save(user);
        } else if (inArray(text, arr("⬅️ Orqaga", "⬅️ Назад"))) {
            start(user);
        } else {
            bot.sendMessage(user.getChatId(), msg.wrongBtn(user.getLang()), kyb.menu(user.getLang()));
        }
    }


    public void menu(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        if (data.equals("yes delete")) {
            ResponseDto<Load> byId = loadService.findById(user.getLoadId());
            Load load = byId.getData();
            load.setActive(false);
            loadService.save(load);
            bot.alertMessage(callbackQuery, msg.successDelete(user.getLang()));
            bot.deleteMessage(user.getChatId(), messageId);
            start(user);

        } else if (data.equals("no delete")) {
            bot.alertMessage(callbackQuery, msg.noDelete(user.getLang()));
            start(user);
        } else {
            Long loadId = Long.valueOf(data);
            ResponseDto<Load> byId = loadService.findById(loadId);
            if (byId.isSuccess()) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(user.getChatId(), msg.loadInformation(user.getLang(), byId.getData()) + "\n\n" + msg.requestDelete(user.getLang()), kyb.isSuccessBtn(user.getLang()));
                user.setLoadId(byId.getData().getId());
                userService.save(user);
            } else {
                bot.alertMessage(callbackQuery, msg.wrongBtn(user.getLang()));
            }
        }
    }

    public void add(User user, String text) {
        String eventCode = user.getEventCode();
        String s;
        Load load;
        ReplyKeyboardMarkup markup = null;
        boolean remove = false;
        switch (eventCode) {
            case "get full name" -> {
                if (inArray(text, arr("⬅️ Orqaga", "⬅️ Назад"))) {
                    start(user);
                    return;
                }
                ResponseDto<Load> loadDto = loadService.findByStatus("draft", user.getId());

                if (loadDto.isSuccess()) {
                    load = loadDto.getData();
                } else {
                    load = new Load();
                    load.setStatus("draft");
                    load.setActive(false);
                }
                load.setUserId(user.getId());
                load.setFullName(text);
                loadService.save(load);
                s = msg.toAddressAndFromAddress(user.getLang());
                remove = true;
                user.setEventCode("get address");
            }
            case "get address" -> {
                load = load(user);
                load.setToAddressAndFromAddress(text);
                s = msg.requestLoadName(user.getLang());
                user.setEventCode("get load name");
                loadService.save(load);
            }
            case "get load name" -> {
                load = load(user);
                load.setName(text);
                s = msg.requestLoadWeight(user.getLang());
                user.setEventCode("get load weight");
                loadService.save(load);
            }
            case "get load weight" -> {
                load = load(user);
                load.setWeight(text);
                s = msg.requestLoadPrice(user.getLang());
                user.setEventCode("get load price");
                loadService.save(load);
            }
            case "get load price" -> {
                load = load(user);
                load.setPrice(text);
                s = msg.requestLoadCarCount(user.getLang());
                user.setEventCode("get load car count");
                loadService.save(load);
            }
            case "get load car count" -> {
                load = load(user);
                try {
                    load.setCarCount(Integer.valueOf(text));
                    s = msg.requestLoadIsAdvance(user.getLang());
                    user.setEventCode("get load is advance");
                    markup = kyb.isSuccess(user.getLang());
                } catch (NumberFormatException e) {
                    s = msg.errorCarCount(user.getLang());
                }
                loadService.save(load);
            }
            case "get load is advance" -> {
                load = load(user);
                if (inArray(text, arr("✅ Ha", "«❌ Да»"))) {
                    s = msg.getAdvancePrice(user.getLang());
                    load.setIsAdvance(true);
                    user.setEventCode("get load advance price");
                    remove = true;
                } else if (inArray(text, arr("❌ Yo'q", "«❌ Нет»"))) {
                    load.setIsAdvance(false);
                    s = msg.paymentType(user.getLang());
                    remove = true;
                    user.setEventCode("get load payment type");
                } else {
                    s = msg.wrongBtn(user.getLang());
                }
                loadService.save(load);
            }
            case "get load advance price" -> {
                s = msg.paymentType(user.getLang());
                load = load(user);
                load.setAdvance(text);
                user.setEventCode("get load payment type");
                loadService.save(load);
            }
            case "get load payment type" -> {
                s = msg.getPhone(user.getLang());
                load = load(user);
                load.setPaymentType(text);
                user.setEventCode("get load phone");
                markup = kyb.setKeyboards(user.getHelperPhone() == null ? arr(user.getPhone()) : arr(user.getPhone(), user.getHelperPhone()), 1);
                loadService.save(load);
            }
            case "get load phone" -> {
                user.setHelperPhone(text);
                load = load(user);
                load.setPhone(text);
                s = msg.loadInformation(user.getLang(), load) + "\n\n" + msg.isTrue(user.getLang());
                markup = kyb.isSuccess(user.getLang());
                user.setEventCode("is add load");
                loadService.save(load);
            }
            case "is add load" -> {
                load = load(user);
                if (inArray(text, arr("✅ Ha", "«❌ Да»"))) {
                    load.setStatus("open");
                    load.setActive(true);
                    s = msg.successAddLoad(user.getLang());
                    loadService.save(load);
                    List<User> admins = userService.findAllByRole("admin", 0, 100).getData().getContent();
                    bot.sendMessage(user.getChatId(), msg.sendMsgToAdmin(user.getLang(), admins));
                    for (User admin : admins) {

                        SendContact sendContact = new SendContact();
                        sendContact(user, admin, sendContact);
                        admin.setEventCode("menu");
                        userService.save(admin);
                        bot.sendMessage(admin.getChatId(), (user.getLang().equals("uz") ? "O'ZBEK" : "RUS") + ", YUKCHI YUKCHI YUKCHI\n\n\n" + RoleLoaderMsg.loadInformation("uz", load), kyb.addLoadForAdmin(load.getId()));
                        sendContact = new SendContact();
                        sendContact(admin, user, sendContact);
                    }
                    bot.sendMessage(user.getChatId(), s, markup);
                    start(user);
                    return;
                } else if (inArray(text, arr("«❌ Нет»", "❌ Yo'q"))) {
                    s = msg.notAddLoad(user.getLang());
                    bot.sendMessage(user.getChatId(), s, markup);
                    start(user);
                    return;
                } else {
                    s = msg.wrongBtn(user.getLang());
                }
                loadService.save(load);
            }
            default -> {
                return;
            }
        }
        if (remove) {
            bot.sendMessage(user.getChatId(), s, true);
        } else {
            bot.sendMessage(user.getChatId(), s, markup);
        }

        userService.save(user);
    }

    private void sendContact(User user, User admin, SendContact sendContact) {
        sendContact.setPhoneNumber(admin.getPhone());
        sendContact.setFirstName(admin.getFirstname());
        sendContact.setLastName(admin.getLastname());
        sendContact.setChatId(user.getChatId());
        try {
            bot.execute(sendContact);
        } catch (TelegramApiException e) {

        }
    }

    private Load load(User user) {
        return loadService.findByStatus("draft", user.getId()).getData();
    }
}
