package uz.zafar.logisticsapplication.bot.role_user_active;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import uz.zafar.logisticsapplication.bot.Function;
import uz.zafar.logisticsapplication.bot.Kyb;
import uz.zafar.logisticsapplication.bot.TelegramBot;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.service.UserService;



@Controller
//@Log4j2
public class UserActiveFunction extends Function {

    public final TelegramBot bot;
    public final UserService userService;
    public final UserActiveKyb kyb;
    public final UserActiveMsg msg;

    public UserActiveFunction(TelegramBot bot, UserService userService, UserActiveKyb kyb, UserActiveMsg msg) {
        this.bot = bot;
        this.userService = userService;
        this.kyb = kyb;
        this.msg = msg;
    }

    public void start(User user) {
        String lang = user.getLang(), phone = user.getPhone();
        if (lang == null) {
            bot.sendMessage(user.getChatId(), msg.requestLang(user.getNickname()), kyb.requestLang);
            user.setEventCode("request lang");
            userService.save(user);
        } else if (phone == null) {
            bot.sendMessage(user.getChatId(), msg.requestContact(user.getLang()), kyb.requestContact(msg.requestContactBtn(user.getLang())));
            user.setEventCode("request contact");
            userService.save(user);
        } else {
            bot.sendMessage(user.getChatId(), msg.menu(user.getLang()), kyb.menuBtn(user.getLang()));
            user.setEventCode("menu");
            userService.save(user);
        }

    }

    public void requestLang(User user, String text, int messageId) {
        String lang;
        if (text.equals("\uD83C\uDDFA\uD83C\uDDFF O'zbek tili")) {
            lang = "uz";
        } else if (text.equals("\uD83C\uDDF7\uD83C\uDDFA Русский язык")) {
            lang = "ru";
        } else {
            bot.deleteMessage(user.getChatId(), messageId);
            return;
        }
        user.setLang(lang);
        userService.save(user);
        bot.sendMessage(user.getChatId(), msg.chooseLang(lang));
        start(user);
    }

    public void requestContact(User user, Contact contact) {
        if (user.getChatId().equals(contact.getUserId())) {
            String p = contact.getPhoneNumber();
            user.setPhone(p.charAt(0) == '+' ? p : ("+" + p));
            start(user);
        } else {
            bot.sendMessage(user.getChatId(), msg.wrongBtn(user.getLang()), kyb.requestContact(user.getLang().equals("uz") ? "\uD83D\uDCDE Ro'yxatdan o'tish" : "\uD83D\uDCDE Зарегистрироваться"));
        }
    }

    public void menu(User user, String text) {
        if (text.equals(UserActiveKyb.menu(user.getLang())[0])) {
            user.setHelperRole("driver");
            bot.sendMessage(user.getChatId(), msg.checkRole(user.getLang(), "driver"), kyb.isSuccess(user.getLang()));
        } else if (text.equals(UserActiveKyb.menu(user.getLang())[1])) {
            user.setHelperRole("loader");
            bot.sendMessage(user.getChatId(), msg.checkRole(user.getLang(), "loader"),kyb.isSuccess(user.getLang()));
        } else if (text.equals(UserActiveKyb.menu(user.getLang())[2])) {
            bot.sendMessage(user.getChatId(), msg.wrongUser(user.getLang()), true);
            user.setRole("block");
            userService.save(user);
            return;
        } else {
            bot.sendMessage(user.getChatId(), msg.wrongUser(user.getLang()), kyb.menuBtn(user.getLang()));
            return;
        }
        user.setEventCode("choose role");
        userService.save(user);
    }

    public void chooseRole(User user, String text) {
        if (inArray(text, new String[]{"✅ Ha", "«❌ Да»"})) {
            user.setRole(user.getHelperRole());
            userService.save(user);

        } else if (inArray(text, new String[]{"❌ Yo'q", "«❌ Нет»"})) {
            start(user);
            return;
        } else {
            bot.sendMessage(user.getChatId(), msg.wrongBtn(user.getLang()), kyb.isSuccess(user.getLang()));
            return;
        }
        bot.sendMessage(user.getChatId(),
                msg.successRole(user.getLang(), user.getLang().equals("uz") ? user.getHelperRole().equals("driver") ? "haydovchi"
                        : "yukchi" : user.getHelperRole().equals("driver") ? "водитель" : "погрузчик"), kyb.setKeyboards(new String[]{"/start"}, 1)
        );
    }

}
