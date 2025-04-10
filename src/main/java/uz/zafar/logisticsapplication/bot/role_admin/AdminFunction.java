package uz.zafar.logisticsapplication.bot.role_admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.zafar.logisticsapplication.bot.Function;
import uz.zafar.logisticsapplication.bot.TelegramBot;
import uz.zafar.logisticsapplication.bot.role_loader.RoleLoaderMsg;
import uz.zafar.logisticsapplication.db.domain.*;
import uz.zafar.logisticsapplication.db.service.*;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;
import java.util.UUID;

import static uz.zafar.logisticsapplication.bot.StaticVariables.*;

@Controller
@Log4j2
public class AdminFunction extends Function {

    private final TelegramBot bot;
    private final UserService userService;
    private final AdminKyb kyb;
    private final ChannelService channelService;
    private final LoadService loadService;
    private final ServiceService serviceService;
    private final CountryService countryService;
    private final OrderService orderService;

    public AdminFunction(TelegramBot bot, UserService userService, AdminKyb kyb, ChannelService channelService, LoadService loadService, ServiceService serviceService, CountryService countryService, OrderService orderService) {
        this.bot = bot;
        this.userService = userService;
        this.kyb = kyb;
        this.channelService = channelService;
        this.loadService = loadService;
        this.serviceService = serviceService;
        this.countryService = countryService;
        this.orderService = orderService;
    }

    public void start(User user) {
        user.setEventCode("menu");
        user.setPage(0);
        userService.save(user);
        bot.sendMessage(user.getChatId(), "Asosiy menyudasiz", kyb.menu);
    }

    public void menu(User user, String text) {
        Long chatId = user.getChatId();
        if (text.equals(adminMenu[0])) {
            user.setEventCode("users page");
            userService.save(user);
            bot.sendMessage(user.getChatId(), text, kyb.usersPage());
        } else if (text.equals(adminMenu[1])) {
            user.setEventCode("reklama");
            userService.save(user);
            bot.sendMessage(user.getChatId(), "Menga reklamangizni yuboring", kyb.back);
        } else if (text.equals(adminMenu[2])) {
            statistika(userService, bot, user.getChatId(), kyb.back);
            start(user);
        } else if (text.equals(adminMenu[3])) {
            List<Channel> list = channelService.findAll().getData();
            if (list.isEmpty()) {
                bot.sendMessage(user.getChatId(), "Kanallar mavjud emas, qo'shish uchun kanal qo'shish tugmasini bosing", kyb.crud);
            } else {
                bot.sendMessage(user.getChatId(), text, kyb.crud);
                bot.sendMessage(user.getChatId(), getChannels(list), kyb.channelIds(list));
            }
            user.setEventCode("channel crud");
            userService.save(user);
        } else if (text.equals(adminMenu[4])) {
            bot.sendMessage(user.getChatId(), "Sizning telefon raqamingiz %s".formatted(user.getHelperPhone() == null ? "Hali telefon raqam o'rnatmagansiz" : user.getHelperPhone()), kyb.editPhone);
            user.setEventCode("phone menu");
            userService.save(user);
        } else if (text.equals(adminMenu[5])) {
            user.setPage(0);
            List<Service> list = serviceService.findAll("uz").getData();
            if (list.isEmpty()) {
                bot.sendMessage(user.getChatId(), "Hozirda xizmatlar mavjud emas, xizmat qo'shish uchun xizmat qo'shish tugmasini bosing", kyb.addServiceBtn);
            } else {
                bot.sendMessage(user.getChatId(), "Sizdagi mavjud xizmatlarning ro'yxati", kyb.getAllServices(list));
            }
        } else if (text.equals(adminMenu[6])) {
            List<Order> list = orderService.findAll().getData();
            if (list.isEmpty()) {
                bot.sendMessage(user.getChatId(), "Hozirda sizda faol buyurtmalar yo'q", kyb.menu);
                return;
            }
            for (Order order : list) {
                User u = userService.findById(order.getUserId()).getData();
                String s = u.getPhone();
                if (u.getHelperPhone() != null) s = s.concat(", " + u.getHelperPhone());
                s = s.concat(", " + order.getPhone());
                bot.sendMessage(user.getChatId(), aboutOrder(order.getId(),
                        u.getLang(), service(order.getServiceId()),
                        country(order.getCountryId()), s, u
                ), kyb.successOrder(order.getId()));
            }
        } else if (text.equals(adminMenu[7])) {
            List<Load> list = loadService.findAllForUser().getData();
            if (list.isEmpty()) {
                bot.sendMessage(user.getChatId(), "Hozirda sizda faol yuklar yo'q", kyb.menu);
                return;
            }
            for (Load load : list) {
                User u = userService.findById(load.getUserId()).getData();
                String s = """
                        
                        Telegram username: %s
                        Telegram nickname: %s
                        Telegram id: %d
                        Telegram chat id: %d
                        Telegram phone: %s
                        """.formatted(
                        u.getUsername() == null ? "Mavjud emas" : "@" + u.getUsername(),
                        u.getNickname(),
                        u.getId(), u.getChatId(), u.getPhone() + (u.getHelperPhone() == null ? "" : ", " + u.getHelperPhone())

                );
                bot.sendMessage(user.getChatId(), (user.getLang().equals("uz") ? "O'ZBEK" : "RUS") + ", YUKCHI YUKCHI YUKCHI\n\n\n" + RoleLoaderMsg.loadInformation("uz", load) + "\n" + s, kyb.addLoadForAdmin(load.getId()));
            }
        } else if (text.equals(logout)) {
            bot.sendMessage(chatId, "Haqiqatdan ham admin paneldan chiqishni xoxlaysizmi ?", kyb.setKeyboards(new String[]{"Ha xoxlayman", "Yo'q xoxlamayman"}, 1));
        } else if (text.equals("Ha xoxlayman")) {
            user.setRole("user_active");
            userService.save(user);
            bot.sendMessage(user.getChatId(), "Admin paneldan chiqdingiz, botni ishga tushirish uchun /start ni bosing", kyb.setKeyboards(new String[]{"/start"}, 1));
        } else if (text.equals("Yo'q xoxlamayman")) {
            start(user);
        } else {
            if (text.equals(backButton)) {
                start(user);
                return;
            }
            if (text.equals("Xizmat qo'shish")) {
                bot.sendMessage(chatId, "Xizmat nomini kiriting, o'zbek tilida", kyb.back);
                user.setEventCode("get new service name");
                userService.save(user);
                return;
            }
            ResponseDto<Service> checkService = serviceService.findByNameUz(text);
            if (checkService.isSuccess()) {
                Service service = checkService.getData();
                String s = serviceInformation(service, countryService.countByServiceId(service.getId()));
                bot.sendMessage(chatId, text, kyb.back);
                bot.sendMessage(chatId, s, kyb.crudService());
                user.setEventCode("crud service");
                user.setServiceId(service.getId());
                userService.save(user);
                return;
            }
            bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.menu);
        }
    }

    private String aboutOrder(Long orderId, String lang, Service service, Country country, String phones, User user) {
        boolean isUz = lang.equals("uz");
        if (country == null) {
            return (isUz ? "🇺🇿 O'zbek" : "🇷🇺 Русский") + """
                    
                    🚖 SHOFYOR
                    🆔 ID: %d
                    📌 Xizmat turi: %s
                    📞 Foydalanuvchining telefon raqami: %s
                    
                    👤 Foydalanuvchi ma'lumotlari:
                    🆔 ID: %d
                    💬 Chat ID: %d
                    🔖 Nickname: %s
                    🔗 Username: %s
                    """.formatted(orderId,
                    isUz ? service.getNameUz() : service.getNameRu(),
                    phones,
                    user.getId(),
                    user.getChatId(),
                    user.getNickname(),
                    (user.getUsername() == null || user.getUsername().isEmpty()) ? "Mavjud emas" : "@" + user.getUsername()
            );

        }
        return (isUz ? "🇺🇿 O'zbek" : "🇷🇺 Русский") + """
                
                🚖 SHOFYOR
                🆔 ID: %d
                📌 Xizmat turi: %s
                🌍 Davlat: %s
                📞 Foydalanuvchining telefon raqami: %s
                
                👤 Foydalanuvchi ma'lumotlari:
                🆔 ID: %d
                💬 Chat ID: %d
                🔖 Nickname: %s
                🔗 Username: %s
                """.formatted(orderId,
                isUz ? service.getNameUz() : service.getNameRu(),
                isUz ? country.getNameUz() : country.getNameRu(),
                phones,
                user.getId(),
                user.getChatId(),
                user.getNickname(),
                (user.getUsername() == null || user.getUsername().isEmpty()) ? "Mavjud emas" : "@" + user.getUsername()
        );
    }


    private String serviceInformation(Service service, int size) {
        return String.format("""
                        📝 Ushbu xizmat haqidagi ma'lumotlar:
                        
                        📌 1. O'zbekcha nomi: %s
                        📌 2. Ruscha nomi: %s
                        🌍 3. Davlatlari soni: %d
                        """,
                service.getNameUz(),  // O'zbekcha nomini olish
                service.getNameRu(),
                size// Ruscha nomini olish
                // Davlatlar sonini olish
        );
    }


    private String getChannels(List<Channel> list) {
        String res = "Barcha kanallarning royxati\n\n";
        for (int i = 0; i < list.size(); i++) {
            Channel channel = list.get(i);
            res = res.concat("%d. <a href='%s'>%s</a>\n".formatted(i + 1, channel.getLink(), channel.getName()));
        }
        return res;
    }

    public void usersPage(User user, String text, int size) {
        Long chatId = user.getChatId();
        user.setPage(0);
        userService.save(user);
        Integer page = user.getPage();
        if (text.equals(backButton)) {
            start(user);
            return;
        } else if (text.equals(userPageMenu[0])) {
            Page<User> pageUser = userService.findAll(page, size).getData();
            if (pageUser.hasContent()) {
                List<User> content = pageUser.getContent();

                bot.sendMessage(chatId, text, true);
                bot.sendMessage(chatId, getMsg(content), kyb.userIds(content));
                user.setEventCode("get all users");
                userService.save(user);
            }
        } else if (text.equals(userPageMenu[1])) {
            bot.sendMessage(chatId, "Biror bir chat id kiriting", kyb.back);
            user.setEventCode("get chat id");
            userService.save(user);
        } else if (text.equals(userPageMenu[2])) {
            bot.sendMessage(chatId, "Qidirayotgan username ni kiriting yoki shunga o'xshash username kiriting", kyb.back);
            user.setEventCode("get username");
            userService.save(user);
        } else if (text.equals(userPageMenu[3])) {
            bot.sendMessage(chatId, "Qidirayotgan nik ni kiriting yoki shunga o'xshash nik kiriting", kyb.back);
            user.setEventCode("get nickname");
            userService.save(user);
        } else if (text.equals(userPageMenu[4])) {
            bot.sendMessage(chatId, "Biror bir id kiriting", kyb.back);
            user.setEventCode("get id");
            userService.save(user);
        } else if (text.equals(userPageMenu[5])) {
            Page<User> pageUser = userService.findAllByRole("driver", page, size).getData();
            if (pageUser.hasContent()) {
                List<User> content = pageUser.getContent();

                bot.sendMessage(chatId, text, true);
                bot.sendMessage(chatId, getMsg(content), kyb.userIds(content));
                user.setEventCode("get all drivers");
                userService.save(user);
            } else {
                bot.sendMessage(user.getChatId(), "Hozirda yopiq foydalanuvchilar mavjud emas", kyb.usersPage());
            }
        } else if (text.equals(userPageMenu[6])) {
            Page<User> pageUser = userService.findAllByRole("user_active", page, size).getData();
            if (pageUser.hasContent()) {
                List<User> content = pageUser.getContent();

                bot.sendMessage(chatId, text, true);
                bot.sendMessage(chatId, getMsg(content), kyb.userIds(content));
                user.setEventCode("get all user active");
                userService.save(user);
            } else {
                bot.sendMessage(user.getChatId(), "Hozirda ochiq foydalanuvchilar mavjud emas", kyb.usersPage());
            }
        } else if (text.equals(userPageMenu[7])) {
            Page<User> pageUser = userService.findAllByRole("block", page, size).getData();
            if (pageUser.hasContent()) {
                List<User> content = pageUser.getContent();

                bot.sendMessage(chatId, text, true);
                bot.sendMessage(chatId, getMsg(content), kyb.userIds(content));
                user.setEventCode("get all block");
                userService.save(user);
            } else {
                bot.sendMessage(user.getChatId(), "Hozirda bloklangan foydalanuvchilar mavjud emas", kyb.usersPage());
            }
        } else if (text.equals(userPageMenu[8])) {
            Page<User> pageUser = userService.findAllByRole("loader", page, size).getData();
            if (pageUser.hasContent()) {
                List<User> content = pageUser.getContent();

                bot.sendMessage(chatId, text, true);
                bot.sendMessage(chatId, getMsg(content), kyb.userIds(content));
                user.setEventCode("get all loaders");
                userService.save(user);
            } else {
                bot.sendMessage(user.getChatId(), "Hozirda yukchilar mavjud emas", kyb.usersPage());
            }
        } else bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.usersPage());
    }

    public void getAllUsers(User user, String data, Integer messageId, CallbackQuery callbackQuery, int size) {
        Integer page = user.getPage();

        if (data.equals("next")) {
            page++;
        } else if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else {
            if (data.equals("back")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.deleteMessage(user.getChatId(), messageId - 1);
                menu(user, adminMenu[0]);
            } else if (data.equals("to back")) {
                Page<User> pageUser = userService.findAll(page, size).getData();
                bot.editMessageText(user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent()));
            } else updateUser(user, data, callbackQuery, messageId);
            return;
        }
        user.setPage(page);
        Page<User> pageUser = userService.findAll(page, size).getData();
        if (pageUser.hasContent()) {
            bot.editMessageText(
                    user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
            );
        } else {
            bot.alertMessage(callbackQuery, "Siz eng oxirgi sahifadasiz");
            return;
        }
        userService.save(user);
    }

    private String getMsg(List<User> users) {
        String msg = "<b>Foydalanuvchilarning ro'yxati</b>\n\n";
        for (int i = 0; i < users.size(); i++) {
            String fullName = users.get(i).getFirstname();
            fullName = fullName.concat(users.get(i).getLastname() == null ? "" : " " + users.get(i).getLastname());

            // Maxsus belgilarni qo‘lda qochirish (&, <, > ni almashtirish)
            fullName = fullName.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");

            msg = msg.concat("%d. <a href=\"tg://user?id=%d\">%s</a>\n".formatted(i + 1, users.get(i).getChatId(), fullName));
        }
        return msg;
    }

    private String userInformation(User user) {
        return """
                Ushbu foydalanuvchining malumoatlari: 
                
                1. Telegramdagi niki: <a href="tg://user?id=%d" >%s</a>
                2. Username: %s
                3. Id: %d
                4. Chat Id: %d
                5. Telefon raqam: %s
                6. Qo'shimcha telefon raqam: %s
                7. Holati: %s
                """.formatted(
                user.getChatId(), user.getFirstname() + (user.getLastname() == null ? "" : " " + user.getLastname()),
                user.getUsername() == null ? "Mavjud emas" : ("@" + user.getUsername()),
                user.getId(), user.getChatId(),
                user.getPhone() == null ? "Mavjud emas" : user.getPhone(),
                user.getHelperPhone() == null ? "Mavjud emas" : user.getHelperPhone(), getCondition(user.getRole())
        );
    }

    private String getCondition(String role) {
        return switch (role) {
            case "admin" -> "Admin";
            case "block" -> "Blokangan";
            case "user_active" -> "Ochiq";
            case "driver" -> "Haydovchi";
            case "loader" -> "Yukchi";
            default -> "Error";
        };
    }

    public void getChatId(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, adminMenu[0]);
        } else {
            try {
                ResponseDto<User> checkUser = userService.checkUser(Long.valueOf(text));
                if (checkUser.isSuccess()) {
                    User u = checkUser.getData();
                    user.setUserId(u.getId());
                    bot.sendMessage(user.getChatId(), userInformation(u), kyb.updateUser(u.getRole()));
                    userService.save(user);
                } else {
                    bot.sendMessage(user.getChatId(), "Bunday chat id ga ega foydalanuvchi topilmadi, boshqa chat id kiriting", kyb.back);
                }
            } catch (Exception e) {
                bot.sendMessage(user.getChatId(), "Chat id faqat sonlardan iborat bolishi kerak", kyb.back);
            }
        }
    }

    public void getChatId(User user, String data, Integer messageId, CallbackQuery callbackQuery, int size) {

        if (data.equals("to back")) {
            bot.deleteMessage(user.getChatId(), messageId);
            usersPage(user, userPageMenu[1], size);
        } else updateUserIdAndChatId(user, data, callbackQuery, messageId);
    }

    private void unblock(User user, Integer messageId, CallbackQuery callbackQuery) {
        User u = userService.findById(user.getUserId()).getData();
        u.setRole("user_active");
        userService.save(u);
        bot.alertMessage(callbackQuery, "Blokdan chiqarildi");
        bot.editMessageText(user.getChatId(), messageId, userInformation(u), kyb.updateUser(u.getRole()));
        userService.save(user);
        bot.sendMessage(u.getChatId(), u.getLang().equals("uz") ? "Tabriklaymiz, siz blokdan chiqarildingiz. Endi bemalol botdan foydalanishingiz mumkin. Botni ishlatish uchun /start ni bosing" : "Поздравляем, вы разблокированы. Теперь вы можете свободно использовать бота. Нажмите /start, чтобы запустить бота.");
    }

    private void closeUser(User user, Integer messageId, CallbackQuery callbackQuery) {
        User u = userService.findById(user.getUserId()).getData();
        u.setRole("user_block");
        userService.save(u);
        bot.alertMessage(callbackQuery, "Muvaffaqiyatli o'zgartirildi");
        bot.editMessageText(user.getChatId(), messageId, userInformation(u), kyb.updateUser(u.getRole()));
        userService.save(user);
    }

    private void block(User user, Integer messageId, CallbackQuery callbackQuery) {
        User u = userService.findById(user.getUserId()).getData();
        u.setRole("block");
        userService.save(u);
        bot.alertMessage(callbackQuery, "Muvaffaqiyatli Bloklandi");
        bot.editMessageText(user.getChatId(), messageId, userInformation(u), kyb.updateUser(u.getRole()));
        userService.save(user);
        bot.sendMessage(u.getChatId(), u.getLang().equals("uz") ? "Endi bot siz uchun ishlamaydi, chunki admin tomonidan bloklandingiz" : "Теперь бот у вас не будет работать так как вы заблокированы администратором");
    }

    private void openUser(User user, Integer messageId, CallbackQuery callbackQuery) {
        User u = userService.findById(user.getUserId()).getData();
        u.setRole("user_active");
        userService.save(u);
        bot.alertMessage(callbackQuery, "Muvaffaqiyatli o'zgartirildi");
        bot.editMessageText(user.getChatId(), messageId, userInformation(u), kyb.updateUser(u.getRole()));

    }

    public void getUsername(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, adminMenu[0]);
        } else {
            ResponseDto<Page<User>> checkByUsername = userService.findAllByUsername(text, user.getPage(), bot.size);
            if (checkByUsername.isSuccess()) {
                if (!checkByUsername.getData().hasContent()) {
                    bot.sendMessage(user.getChatId(), "Bunday username ga o'xshash username topilmadi. Boshqa username kiriting", kyb.back);
                    return;
                }
                user.setFindUsername(text);
                bot.sendMessage(user.getChatId(), getMsg(checkByUsername.getData().getContent()), kyb.userIds(checkByUsername.getData().getContent()));
                userService.save(user);
            } else
                bot.sendMessage(user.getChatId(), "Bunday username ga o'xshash username topilmadi. Boshqa username kiriting", kyb.back);
        }
    }

    public void getUsername(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        Integer page = user.getPage();
        if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else if (data.equals("next")) {
            page++;
        } else {
            if (data.equals("back")) {
                bot.deleteMessage(user.getChatId(), messageId);
                usersPage(user, userPageMenu[2], bot.size);
            } else if (data.equals("to back")) {

                Page<User> pageUser = userService.findAllByUsername(user.getFindUsername(), page, bot.size).getData();
                bot.editMessageText(
                        user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
                );
            } else {
                updateUser(user, data, callbackQuery, messageId);
            }
            return;
        }
        user.setPage(page);
        Page<User> pageUser = userService.findAllByUsername(user.getFindUsername(), page, bot.size).getData();
        if (pageUser.hasContent()) {
            bot.editMessageText(
                    user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
            );
        } else {
            bot.alertMessage(callbackQuery, "Siz eng oxirgi sahifadasiz");
            return;
        }
        userService.save(user);
    }

    private void updateUser(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        switch (data) {
            case "block" -> block(user, messageId, callbackQuery);
            case "open block" -> unblock(user, messageId, callbackQuery);
            default -> {
                Long userId = Long.valueOf(data);
                user.setUserId(userId);
                User u = userService.findById(userId).getData();
                bot.editMessageText(user.getChatId(), messageId, userInformation(u), kyb.updateUser(u.getRole()));
                userService.save(user);
            }
        }
    }

    public void getNickname(User user, String text) {
        if (text.equals(backButton)) menu(user, adminMenu[0]);
        else {
            ResponseDto<Page<User>> checkByNickname = userService.findAllByNickname(text, user.getPage(), bot.size);
            if (checkByNickname.isSuccess()) {
                if (!checkByNickname.getData().hasContent()) {
                    bot.sendMessage(user.getChatId(), "Bunday nikka o'xshash foydalanuvchi topilmadi. Boshqa nik kiriting", kyb.back);
                    return;
                }
                user.setFindNickname(text);
                bot.sendMessage(user.getChatId(), getMsg(checkByNickname.getData().getContent()), kyb.userIds(checkByNickname.getData().getContent()));
                userService.save(user);
            } else
                bot.sendMessage(user.getChatId(), "Bunday nikka o'xshash foydalanuvchi topilmadi. Boshqa nik kiriting", kyb.back);
        }
    }

    public void getNickname(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        Integer page = user.getPage();
        if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else if (data.equals("next")) {
            page++;
        } else {
            if (data.equals("back")) {
                bot.deleteMessage(user.getChatId(), messageId);
                usersPage(user, userPageMenu[3], bot.size);
            } else if (data.equals("to back")) {
                Page<User> pageUser = userService.findAllByNickname(user.getFindNickname(), page, bot.size).getData();

                bot.editMessageText(
                        user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
                );
                return;
            } else {
                updateUser(user, data, callbackQuery, messageId);
            }
            return;
        }
        user.setPage(page);
        Page<User> pageUser = userService.findAllByNickname(user.getNickname(), page, bot.size).getData();
        if (pageUser.hasContent()) {
            bot.editMessageText(
                    user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
            );
        } else {
            bot.alertMessage(callbackQuery, "Siz eng oxirgi sahifadasiz");
            return;
        }
        userService.save(user);
    }

    public void getId(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, adminMenu[0]);
        } else {
            try {
                ResponseDto<User> checkUser = userService.findById(Long.valueOf(text));
                if (checkUser.isSuccess()) {
                    User u = checkUser.getData();
                    user.setUserId(u.getId());
                    bot.sendMessage(user.getChatId(), userInformation(u), kyb.updateUser(u.getRole()));
                    userService.save(user);
                } else {
                    bot.sendMessage(user.getChatId(), "Bunday id ga ega foydalanuvchi topilmadi, boshqa chat id kiriting", kyb.back);
                }
            } catch (Exception e) {
                bot.sendMessage(user.getChatId(), "Id faqat sonlardan iborat bolishi kerak", kyb.back);
            }
        }
    }

    public void getId(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        if (data.equals("to back")) {
            bot.deleteMessage(user.getChatId(), messageId);
            usersPage(user, userPageMenu[4], bot.size);
        } else {
            updateUserIdAndChatId(user, data, callbackQuery, messageId);
        }
    }

    private void updateUserIdAndChatId(User user, String data, CallbackQuery callbackQuery, Integer messageId) {
        switch (data) {
            case "block" -> block(user, messageId, callbackQuery);
            case "open block" -> unblock(user, messageId, callbackQuery);
        }
    }

    public void getAllUserBlock(User user, String data, CallbackQuery callbackQuery, Integer messageId, String role) {
        Integer page = user.getPage();

        if (data.equals("next")) {
            page++;
        } else if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else {
            if (data.equals("back")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.deleteMessage(user.getChatId(), messageId - 1);
                menu(user, adminMenu[0]);
            } else if (data.equals("to back")) {
                Page<User> pageUser = userService.findAllByRole(role, page, bot.size).getData();
                bot.editMessageText(user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent()));
            } else updateUser(user, data, callbackQuery, messageId);
            return;
        }
        user.setPage(page);
        Page<User> pageUser = userService.findAllByRole(role, page, bot.size).getData();
        if (pageUser.hasContent()) {
            bot.editMessageText(
                    user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
            );
        } else {
            bot.alertMessage(callbackQuery, "Siz eng oxirgi sahifadasiz");
            return;
        }
        userService.save(user);
    }

    public void reklama(User user, Integer messageId) {
        user.setMessageId(messageId);
        user.setEventCode("is send reklama");
        userService.save(user);
        bot.sendMessage(user.getChatId(), "Haqiqatdan ham ushbu reklamani hammaga yubormoqchimisiz ?", kyb.setKeyboards(new String[]{"Ha yubormoqchiman", "Yo'q yubormoqchi emasman"}, 1));
    }

    public void isSendReklama(User user, String text) {
        if (text.equals("Ha yubormoqchiman")) {
            long count = 0, allCount = -1;
            for (User datum : userService.findAll().getData()) {
                allCount++;
                try {
                    bot.execute(
                            CopyMessage
                                    .builder()
                                    .chatId(datum.getChatId())
                                    .fromChatId(user.getChatId())
                                    .messageId(user.getMessageId())
                                    .replyMarkup(kyb.setKeyboards(new String[]{"/start"}, 1))
                                    .build()
                    );
                    count++;
                } catch (Exception e) {

                }
            }
            bot.sendMessage(user.getChatId(), "Sizning reklamangiz %d  kishidan %d kishiga muvaffaqiyatli yuborildi".formatted(allCount, count));
            start(user);
        } else if (text.equals("Yo'q yubormoqchi emasman")) {
            bot.sendMessage(user.getChatId(), "Rekamangiz yuborilmadi");
            start(user);
        } else
            bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.setKeyboards(new String[]{"Ha yubormoqchiman", "Yo'q yubormoqchi emasman"}, 1));
    }

    public void channelCrud(User user, String text) {
        if (text.equals(addChannel)) {
            bot.sendMessage(user.getChatId(), "Kanal nomini kiriting", kyb.back);
            user.setEventCode("add new channel name");
            userService.save(user);
        } else if (text.equals(backButton)) {
            start(user);
        } else bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.crud);
    }

    public void addNewChannelName(User user, String text) {
        if (text.equals(backButton)) {
            menu(user, adminMenu[3]);
            return;
        }
        ResponseDto<Channel> checkChannel = channelService.findByStats("draft");
        Channel channel;
        if (checkChannel.isSuccess()) {
            channel = checkChannel.getData();
        } else {
            channel = new Channel();
            channel.setActive(false);
            channel.setStatus("draft");
        }
        channel.setName(text);
        channelService.save(channel);
        bot.sendMessage(user.getChatId(), "Kanal id sini yoki username sini kiriting", true);
        user.setEventCode("add new channel username");
        userService.save(user);

    }

    private Channel channel() {
        return
                channelService.findByStats("draft").getData();
    }

    private Channel channel(Long channelId) {
        return
                channelService.findById(channelId).getData();
    }

    public void addNewChannelUsername(User user, String text) {
        Channel channel = channel();
        channel.setChannelId(text);
        channelService.save(channel);
        bot.sendMessage(user.getChatId(), "Muvaffaqiyatli saqlandi, endi kanal havolasini yuboring");
        user.setEventCode("add new channel link");
        userService.save(user);


    }

    public void addNewChannelLink(User user, String text) {
        Channel channel = channel();
        channel.setLink(text);
        channelService.save(channel);
        bot.sendMessage(user.getChatId(), "Muvaffaqiyatli saqlandi, endi kanal havolasini yuboring");
        user.setEventCode("add new channel link");
        userService.save(user);
        bot.sendMessage(user.getChatId(), channelInformation(channel) + "\n\nUshbu kanalni haqiqatdan ham qo'shmoqchimisiz ?", kyb.setKeyboards(new String[]{"Ha", "Yo'q"}, 1));
        user.setEventCode("is add new channel");
        userService.save(user);
    }

    private String channelInformation(Channel channel) {
        return """
                Kanal nomi: %s
                Kanal id(userame) si: %s
                Kanal link: %s
                """.formatted(channel.getName(), channel.getChannelId(), channel.getLink());
    }

    public void isAddNewChannel(User user, String text) {
        Channel channel = channel();
        if (text.equals("Ha")) {
            channel.setActive(true);
            channel.setStatus("open");
            channelService.save(channel);
            bot.sendMessage(user.getChatId(), "Muvaffaqiyatli qo'shildi");
        } else if (text.equals("Yo'q")) {
            bot.sendMessage(user.getChatId(), "Ushbu kanal qo'shilmadi");
        } else {
            bot.sendMessage(user.getChatId(), "Iltimos, tugmalardan foydalaning", kyb.setKeyboards(new String[]{"Ha", "Yo'q"}, 1));
            return;
        }
        start(user);
    }

    public void channelCrud(User user, String data, CallbackQuery callbackQuery, Integer messageId) {

        if (data.equals("back")) {
            bot.deleteMessage(user.getChatId(), messageId);
            start(user);
        } else {
            if (data.equals("delete")) {
                bot.editMessageText(user.getChatId(), messageId, channelInformation(channel(user.getChannelId())) + "\n\nUshbu kanalni haqiqatdan ham o'chirmoqchimisiz ?", kyb.isSuccess());
            } else if (data.equals("yes")) {
                Channel channel = channel(user.getChannelId());
                channel.setActive(false);
                channelService.save(channel);
                bot.alertMessage(callbackQuery, "Muvaffaqiyatli o'chirildi");
                bot.editMessageText(user.getChatId(), messageId, getChannels(channelService.findAll().getData()), kyb.channelIds(channelService.findAll().getData()));
            } else if (data.equals("edit name")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(user.getChatId(), "Avvalgi nomi: <code>%s</code>\n\nYangi nomini kiriting".formatted(channel(user.getChannelId()).getName()), kyb.back);
                user.setEventCode("edit channel name");
                userService.save(user);
            } else if (data.equals("edit channel id")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(user.getChatId(), "Avvalgi kanal id(username) si: <code>%s</code>\n\nYangi id(username) sini kiriting".formatted(channel(user.getChannelId()).getChannelId()), kyb.back);
                user.setEventCode("edit channel id");
                userService.save(user);
            } else if (data.equals("edit channel link")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(user.getChatId(), "Avvalgi kanal havolasi: <code>%s</code>\n\nYangi kanal havolasini kiriting".formatted(channel(user.getChannelId()).getLink()), kyb.back);
                user.setEventCode("edit channel link");
                userService.save(user);
            } else if (data.equals("no")) {
                bot.alertMessage(callbackQuery, "O'chirilmadi");
                Channel channel = channelService.findById(user.getChannelId()).getData();

                bot.editMessageText(user.getChatId(), messageId, channelInformation(channel), kyb.channelCrud());
            } else if (data.equals("back1")) {
                bot.editMessageText(user.getChatId(), messageId, getChannels(channelService.findAll().getData()), kyb.channelIds(channelService.findAll().getData()));
            } else {
                user.setChannelId(Long.valueOf(data));
                Long channelId = user.getChannelId();
                Channel channel = channelService.findById(channelId).getData();
                bot.editMessageText(user.getChatId(), messageId, channelInformation(channel), kyb.channelCrud());
                userService.save(user);
            }
        }
    }

    public void editChannel(User user, String text, String eventCode) {
        Channel channel = channel(user.getChannelId());
        if (!text.equals(backButton))
            switch (eventCode) {
                case "edit channel name" -> channel.setName(text);
                case "edit channel id" -> channel.setChannelId(text);
                case "edit channel link" -> channel.setLink(text);
            }
        channelService.save(channel);
        bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'zgartirildi\n\n\n" + channelInformation(channel), kyb.channelCrud());
        user.setEventCode("channel crud");
        userService.save(user);
    }

    public void getAllLoaders(User user, String data, CallbackQuery callbackQuery, Integer messageId, String role) {
        Integer page = user.getPage();

        if (data.equals("next")) {
            page++;
        } else if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else {
            if (data.equals("back")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.deleteMessage(user.getChatId(), messageId - 1);
                menu(user, adminMenu[0]);
            } else if (data.equals("to back")) {
                Page<User> pageUser = userService.findAllByRole(role, page, bot.size).getData();
                bot.editMessageText(user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent()));
            } else updateUser(user, data, callbackQuery, messageId);
            return;
        }
        user.setPage(page);
        Page<User> pageUser = userService.findAllByRole(role, page, bot.size).getData();
        if (pageUser.hasContent()) {
            bot.editMessageText(
                    user.getChatId(), messageId, getMsg(pageUser.getContent()), kyb.userIds(pageUser.getContent())
            );
        } else {
            bot.alertMessage(callbackQuery, "Siz eng oxirgi sahifadasiz");
            return;
        }
        userService.save(user);
    }

    public void phoneMenu(User user, String text) {
        if (text.equals(backButton)) {
            start(user);
            return;
        }

        if (text.equals("Telefon raqamni o'zgartirish")) {
            bot.sendMessage(user.getChatId(), "Telefon raqamingizni kiriting", kyb.setPhoneNumbers(user.getPhone(), user.getHelperPhone()));
            return;
        }
        user.setHelperPhone(text);
        userService.save(user);
        bot.sendMessage(user.getChatId(), "Muvaffaqiyatli o'zgartirildi");
        menu(user, adminMenu[4]);
    }

    public void menu(User user, String data, Integer messageId, CallbackQuery callbackQuery) {
        try {
            String[] s = data.split("_");
            if (s[0].equals("successorder")) {
                Order order = orderService.findById(Long.valueOf(s[1])).getData();
                if (!order.getActive()) {
                    bot.alertMessage(callbackQuery, "Sizdan oldin kimdir yakunlaganga o'xshaydu");
                    bot.deleteMessage(user.getChatId(), messageId);
                    return;
                }
                order.setActive(false);
                orderService.save(order);
                bot.alertMessage(callbackQuery, "Muvaffaqiyatli yakunladingiz");
                bot.deleteMessage(user.getChatId(), messageId);
                start(user);
                return;
            }
        } catch (Exception e) {

        }
        Load load = loadService.findById(Long.valueOf(data)).getData();
        if (load.getActive()) {
            load.setActive(false);
            loadService.save(load);
            bot.alertMessage(callbackQuery, "Muvaffaqiyatli yakunladingiz");
            bot.deleteMessage(user.getChatId(), messageId);
        } else {
            bot.alertMessage(callbackQuery, "Buni sizdan oldin kimdir yakunlaganga o'xshaydi");
            bot.deleteMessage(user.getChatId(), messageId);
        }
    }

    public void getNewServiceName(User user, String text) {

        if (text.equals(backButton)) {
            user.setEventCode("menu");
            userService.save(user);
            menu(user, adminMenu[5]);
        } else {
            Service service;
            ResponseDto<Service> checkService = serviceService.findByStatus("draft");
            if (checkService.isSuccess()) {
                service = checkService.getData();
            } else {
                service = new Service();
                service.setStatus("draft");
                service.setActive(false);
            }
            service.setNameUz(text);
            ResponseDto<Void> save = serviceService.save(service);
            if (!save.isSuccess()) {
                bot.sendMessage(user.getChatId(), "Bu nom band, iltimos boshqa nom kiriting");
                return;
            }
            bot.sendMessage(user.getChatId(), "Ushbu xizmatning ruscha nomini kiriting", true);
            user.setEventCode("get new service name ru");
            userService.save(user);
        }
    }

    public void getNewServiceNameRu(User user, String text) {
        Service service = service();
        service.setNameRu(text);
        service.setStatus("open");
        service.setActive(true);
        ResponseDto<Void> save = serviceService.save(service);
        if (!save.isSuccess()) {
            bot.sendMessage(user.getChatId(), "Bu nom band, iltimos boshqa nom kiriting");
            return;
        }
        bot.sendMessage(user.getChatId(), "Muvaffaqiyatli qo'shildi");
        user.setEventCode("menu");
        userService.save(user);
        menu(user, adminMenu[6 - 1]);
    }

    private Service service() {
        return serviceService.findByStatus("draft").getData();
    }

    private Service service(Long id) {
        return serviceService.findById(id).getData();
    }

    public void crudService(User user, CallbackQuery callbackQuery, Integer messageId, String data) {
        if (data.equals(backButton)) {
            start(user);
        } else if (data.equals("Xizmat qo'shish")) {
            menu(user, data);
        } else {
            if (data.equals("edit name uz") || data.equals("edit name ru")) {
                Service service = service(user.getServiceId());
                user.setEventCode(data);
                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(user.getChatId(), "Avvalgi nomi: <code>%s</code>\n\nYangi nomini kiriting".formatted(
                        data.equals("edit name uz") ? service.getNameUz() : service.getNameRu()
                ));
                userService.save(user);
            } else if (data.equals("delete")) {
                Service service = service(user.getServiceId());
                service.setActive(false);
                service.setNameUz(UUID.randomUUID().toString() + service.getId());
                service.setNameRu(UUID.randomUUID().toString() + service.getId());
                ResponseDto<Void> save = serviceService.save(service);
                if (!save.isSuccess()) {
                    bot.alertMessage(callbackQuery, "Nimagadir o'chmadi, o'chirishda muammo chiqdi chekilli");
                } else {
                    bot.alertMessage(callbackQuery, "Muvaffaqiyatli o'chirildi");
                    bot.deleteMessage(user.getChatId(), messageId);
                    user.setEventCode("menu");
                    userService.save(user);
                    menu(user, adminMenu[6 - 1]);
                }
            } else if (data.equals("seen country lists")) {
                Page<Country> countryPage = countryService.findAllByService(service(user.getServiceId()).getId(), user.getPage(), bot.size, "uz").getData();
                boolean hasCountries = !countryPage.getContent().isEmpty();
                bot.editMessageText(user.getChatId(), messageId,
                        getFromServiceCountriesInformation(user.getServiceId(), hasCountries),
                        kyb.setCountries(countryPage.getContent(), "uz", true));
            } else if (data.equals("add_country")) {
                bot.deleteMessage(user.getChatId(), messageId);
                bot.sendMessage(user.getChatId(), "%s\n\nDavlat nomini kiriting(uzbek tilida)".formatted(service(user.getServiceId()).getNameUz()), kyb.back);
                user.setEventCode("add country uz");
                userService.save(user);
            } else if (data.equals("delete_all_countries")) {
                Service service = service(user.getServiceId());
                List<Country> all = countryService.findAllByService(service.getId(), user.getPage(), countryService.countByServiceId(user.getServiceId()), "uz").getData().getContent();
                if (all.isEmpty()) {
                    bot.alertMessage(callbackQuery, "O'chirish uchun davlat mavjud emas");
                    return;
                }
                for (Country uz : all) {
                    uz.setActive(false);
                    countryService.save(uz);
                }
                bot.alertMessage(callbackQuery, "Barcha davlatlar o'chirildi");
                bot.editMessageText(user.getChatId(), messageId, getFromServiceCountriesInformation(user.getServiceId(), false), kyb.setCountries(countryService.findAllByService(service(user.getServiceId()).getId(), user.getPage(), bot.size, "uz").getData().getContent(), "uz", true));
            } else if (data.equals("back")) {
                bot.deleteMessage(user.getChatId(), messageId);
                user.setEventCode("menu");
                userService.save(user);
                menu(user, adminMenu[6 - 1]);
            } else if (data.equals("delete country")) {
                Country country = country(user.getCountryId());
                country.setActive(false);
                countryService.save(country);
                Page<Country> countryPage = countryService.findAllByService(service(user.getServiceId()).getId(), user.getPage(), bot.size, "uz").getData();
                boolean hasCountries = !countryPage.getContent().isEmpty();
                bot.alertMessage(callbackQuery, "Muvaffaquyatlli o'chirildi");
                bot.editMessageText(user.getChatId(), messageId,
                        getFromServiceCountriesInformation(user.getServiceId(), hasCountries),
                        kyb.setCountries(countryPage.getContent(), "uz", true));
            } else if (data.equals("back1")) {
                bot.editMessageText(user.getChatId(), messageId,
                        getFromServiceCountriesInformation(user.getServiceId(), true),
                        kyb.setCountries(countryService.findAllByService(service(user.getServiceId()).getId(), user.getPage(), bot.size, "uz").getData().getContent(), "uz", true));
            } else {
                if (data.equals("next")) {
                    user.setPage(user.getPage() + 1);
                } else if (data.equals("prev")) {
                    if (user.getPage() == 0) {
                        bot.alertMessage(callbackQuery, "Siz eng birinchi sahifadasiz");
                        return;
                    }
                    user.setPage(user.getPage() - 1);
                } else {
                    if (data.equals("to back")) {
                        bot.editMessageText(user.getChatId(), messageId, serviceInformation(service(user.getServiceId()), countryService.countByServiceId(service(user.getServiceId()).getId())), kyb.crudService());
                        return;
                    } else {
                        Long countryId = Long.valueOf(data);
                        user.setCountryId(countryId);
                        userService.save(user);
                        ResponseDto<Country> checkCountry = countryService.findById(countryId);
                        Country country = checkCountry.getData();
                        bot.editMessageText(user.getChatId(), messageId, countryInformation(country, service(user.getServiceId()).getNameUz()), kyb.crudCountry(countryId));
                        return;
                    }
                }
                Page<Country> countryPage = countryService.findAllByService(service(user.getServiceId()).getId(), user.getPage(), bot.size, "uz").getData();
                boolean hasCountries = !countryPage.getContent().isEmpty();
                if (!hasCountries) {
                    bot.alertMessage(callbackQuery, "Siz eng oxirgi sahifadasiz");
                    return;
                }
                userService.save(user);
                bot.editMessageText(user.getChatId(), messageId,
                        getFromServiceCountriesInformation(user.getServiceId(), true),
                        kyb.setCountries(countryPage.getContent(), "uz", true));
            }
        }
    }

    private String countryInformation(Country country, String serviceName) {
        return """
                <b>Ushbu davlat haqidagi ma'lumotlar</b>
                
                <b>O'zbekcha nomi:</b> <i>%s</i>
                
                <b>Ruscha nomi:</b> <i>%s</i>
                
                <b>Xizmat:</b> <i>%s</i>
                """.formatted(
                country.getNameUz(), country.getNameRu(), serviceName
        );
    }

    private Country country(Long cId) {
        return countryService.findById(cId).getData();
    }

    private String getFromServiceCountriesInformation(Long serviceId, boolean hasCountries) {
        Service service = service(serviceId);
        return """
                <b>%s</b>(<b>%s</b>) dagi davlatlar ro'yxati
                
                Umumiy davlatlarining soni <b>%d</b> ta
                
                """.formatted(hasCountries ? service.getNameUz() : "Iltimos, davlat qo'shing. Buyerda hali hamon davlatlar mavjud emas\n" + service.getNameUz(), service.getNameRu(), countryService.countByServiceId(serviceId));
    }

    public void editService(User user, String text, String eventCode) {
        Service service = service(user.getServiceId());
        if (eventCode.equals("edit name uz")) {
            service.setNameUz(text);
        } else {
            service.setNameRu(text);
        }
        ResponseDto<Void> save = serviceService.save(service);
        if (!save.isSuccess()) {
            bot.sendMessage(user.getChatId(), "Bu nom band, iltimos boshqa nom kiriting");
            return;
        }
        menu(user, service.getNameUz());
    }

    public void addCountry(User user, String text, String eventCode) {
        if (text.equals(backButton)) {
            menu(user, service(user.getServiceId()).getNameUz());
            return;
        }
        ResponseDto<Country> countryDto = countryService.findByStatus("draft");
        Country country;
        if (countryDto.isSuccess()) country = countryDto.getData();
        else {
            country = new Country();
            country.setActive(false);
            country.setStatus("draft");
        }
        if (eventCode.equals("add country uz")) {
            country.setServiceId(user.getServiceId());
            country.setNameUz(text);
            ResponseDto<Void> save = countryService.save(country);
            if (!save.isSuccess()) {
                bot.sendMessage(user.getChatId(), save.getMessage());
                return;
            }
            bot.sendMessage(user.getChatId(), "%s davlatining ruscha nomini kiriting".formatted(text), true);
            user.setEventCode("add country ru");
            userService.save(user);
        } else {
            country.setNameRu(text);
            country.setActive(true);
            country.setStatus("open");
            ResponseDto<Void> save = countryService.save(country);
            if (!save.isSuccess()) {
                bot.sendMessage(user.getChatId(), save.getMessage());
                return;
            }
            bot.sendMessage(user.getChatId(), "%s davlatining ruscha nomini kiriting".formatted(text), true);
            menu(user, service(user.getServiceId()).getNameUz());
        }
    }
}
