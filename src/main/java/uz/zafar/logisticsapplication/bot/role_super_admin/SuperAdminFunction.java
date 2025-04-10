package uz.zafar.logisticsapplication.bot.role_super_admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.zafar.logisticsapplication.bot.Function;
import uz.zafar.logisticsapplication.bot.TelegramBot;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.service.UserService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

@Controller
@Log4j2
public class SuperAdminFunction extends Function {
    private final TelegramBot bot;
    private final UserService userService;
    private final SuperAdminKyb kyb;

    public SuperAdminFunction(TelegramBot bot, UserService userService, SuperAdminKyb kyb) {
        this.bot = bot;
        this.userService = userService;
        this.kyb = kyb;
    }

    public void start(User user) {
        bot.sendMessage(user.getChatId(), "Asosiy menyudasiz, o'zingizga kerakli manyulardan birini tanlang", kyb.menu);
        user.setEventCode("menu");
        user.setPage(0);
        userService.save(user);
    }

    private void eventCode(User user, String eventCode) {
        user.setEventCode(eventCode);
        userService.save(user);
    }

    public void menu(User user, String text, int size) {
        if (text.equals("\uD83D\uDD19 Orqaga qaytish")){
            start(user);
            return;
        }
        String[] menu = new String[]{
                "Foydalanuvchilar bo'limi", "Reklama bo'limi", "Statistika bo'limi", "Foydalanuvchilarni chat id bo'yicha qidirish"
        };
        Long chatId = user.getChatId();
        Integer page = user.getPage();
        if (menu[0].equals(text)) {
            ResponseDto<Page<User>> dto = userService.findAll(page, size);
            if (!dto.isSuccess()) {
                bot.sendMessage(chatId, dto.getMessage());
                return;
            }
            List<User> users = dto.getData().getContent();
            user.setPage(0);
            user.setEventCode("users page");
            userService.save(user);
            bot.sendMessage(chatId, "Quyidagi foydalanuvchilardan birini tanlang", true);
            bot.sendMessage(chatId, getUsersText(users), kyb.userIds(users));
        } else if (menu[1].equals(text)) {
            //hali qilinadi
            bot.sendMessage(chatId, "Reklamangizni yuboring", kyb.back);
            user.setEventCode("reklama");
            userService.save(user);
        } else if (menu[2].equals(text)) {
            statistika(userService, bot, user.getChatId(), kyb.back);
        } else if (menu[3].equals(text)) {
            user.setEventCode("get chat id");
            userService.save(user);
            bot.sendMessage(chatId, "biror bir chat id kiriting", kyb.back);
        } else {
            errorBtn(chatId, kyb.menu);
        }
    }

    private void errorBtn(long userId, ReplyKeyboardMarkup markup) {
        bot.sendMessage(userId, "❌ Iltimos, tugmalardan foydalaning", markup);
    }

    public void usersPage(Long chatId, int messageId) {
        bot.deleteMessage(chatId, messageId);
    }


    public void usersPage(User user, String data, Integer messageId, CallbackQuery callbackQuery, int size) {
        Long chatId = user.getChatId();
        Integer page = user.getPage();
        if (data.equals("back")) {
            bot.deleteMessage(chatId, messageId);
            start(user);
            return;
        } else if (data.equals("next")) {
            page++;
        } else if (data.equals("prev")) {

            if (page == 0) {
                bot.alertMessage(callbackQuery, "Siz allaqachon 1-sahifadasiz");
                return;
            }
            page--;
        } else if (data.equals("back1")) {
            ResponseDto<Page<User>> dto = userService.findAll(page, size);
            bot.editMessageText(chatId, messageId, getUsersText(dto.getData().getContent()), kyb.userIds(dto.getData().getContent()));
        } else if (data.equals("admin")) {
            ResponseDto<User> userDto = userService.findById(user.getUserId());
            if (userDto.isSuccess()) {
                User data1 = userDto.getData();
                data1.setPage(0);
                data1.setEventCode("menu");
                data1.setRole("admin");
                userService.save(data1);
                bot.sendMessage(data1.getChatId(), "Tabriklaymiz. Siz admin qilindingiz. Botni ishlatish uchun /start tugmasini bosing", true);
                String msg = """
                        Ushbu foydalanuvchilarning malumotlari.
                        
                        Telegramdagi nik: %s
                        Telegram username: %s
                        Foydalanuvchining chat id si: %d
                        Holati: %s
                        """.formatted(data1.getFirstname() + (data1.getLastname() == null ? "" : " " + data1.getLastname()), data1.getUsername() == null ? "Mavjud emas" : "@" +
                        data1.getUsername(), data1.getChatId(), (data1.getRole()).equals("user_active") ? "Faol" : (data1.getRole().equals("driver") ? "Haydovchi" : (
                        data1.getRole().equals("loader") ? "Yukchi" : "Admin")));
                try {
                    bot.editMessageText(chatId, messageId, msg, kyb.updateUser(data1.getRole().equals("admin")));
                } catch (Exception e) {
                    bot.sendMessage(chatId, e.getMessage());
                }
                bot.alertMessage(callbackQuery, "Operatsiya muvaffaqiyatli yakunlandi");
            } else {
                bot.alertMessage(callbackQuery, "Kutilmagan xatolik");
            }
            return;
        } else if (data.equals("user_active")) {
            ResponseDto<User> userDto = userService.findById(user.getUserId());
            if (userDto.isSuccess()) {
                User data1 = userDto.getData();
                data1.setPage(0);
                data1.setEventCode("menu");
                data1.setRole("user_active");
                userService.save(data1);
                bot.sendMessage(data1.getChatId(), "Siz adminlikdan olindingiz. Botni ishlatish uchun /start tugmasini bosing", true);
                String msg = """
                        Ushbu foydalanuvchilarning malumotlari
                        
                        Telegramdagi nik: %s
                        Telegram username: %s
                        Foydalanuvchining chat id si: %d
                        Holati: %s
                        """.formatted(data1.getFirstname() + (data1.getLastname() == null ? "" : " " + data1.getLastname()), data1.getUsername() == null ? "Mavjud emas" :
                        "@" + data1.getUsername(), data1.getChatId(), (data1.getRole()).equals("user_active") ? "Faol" : (data1.getRole().equals("driver") ? "Haydovchi" : (
                        data1.getRole().equals("loader") ? "Yukchi" : "Admin")));
                bot.editMessageText(chatId, messageId, msg, kyb.updateUser(data1.getRole().equals("admin")));
                bot.alertMessage(callbackQuery, "Operatsiya muvaffaqiyatli yakunlandi");
            } else {
                bot.alertMessage(callbackQuery, "Kutilmagan xatolik");
            }
            return;
        } else {
            ResponseDto<User> dto = userService.findById(Long.valueOf(data));
            if (!dto.isSuccess()) {
                bot.alertMessage(callbackQuery, dto.getMessage());
                return;
            }
            User user1 = dto.getData();
            user.setUserId(user1.getId());
            userService.save(user);
            String msg = """
                    Ushbu foydalanuvchilarning malumotlari
                    
                    Telegramdagi nik: %s
                    Telegram username: %s
                    Foydalanuvchining chat id si: %d
                    Holati: %s
                    """.formatted(user1.getFirstname() + (user1.getLastname() == null ? "" : " " + user1.getLastname()),
                    user1.getUsername() == null ? "Mavjud emas" : "@" + user1.getUsername(), user1.getChatId(),
                    (user1.getRole()).equals("user_active") ? "Faol" : (user1.getRole().equals("driver") ? "Haydovchi" : (
                            user1.getRole().equals("loader") ? "Yukchi" : "Admin")));
            bot.editMessageText(chatId, messageId, msg, kyb.updateUser(user1.getRole().equals("admin")));
            return;
        }
        ResponseDto<Page<User>> dto = userService.findAll(page, size);
        if (!dto.isSuccess()) {
            bot.sendMessage(chatId, dto.getMessage());
            return;
        }
        Page<User> dtoData = dto.getData();
        if (dtoData.isEmpty()) {
            bot.alertMessage(callbackQuery, "Siz oxirgi malumotlardasiz");
            return;
        }
        List<User> users = dto.getData().getContent();
        user.setPage(page);

        userService.save(user);
        bot.editMessageText(user.getChatId(), messageId, getUsersText(users), kyb.userIds(users));
    }

    private String getUsersText(List<User> users) {
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

    public void reklama(User user, Long chatId, Integer messageId) {
        long allUsersCount = userService.findAll().getData().size(), count = 0;
        for (User datum : userService.findAll().getData()) {
            try {
                CopyMessage copyMessage = new CopyMessage();
                copyMessage.setChatId(datum.getChatId());
                copyMessage.setFromChatId(chatId);
                copyMessage.setMessageId(messageId);
                bot.execute(copyMessage);
                count++;
            } catch (Exception ignore) {
            }
        }
        bot.sendMessage(chatId, "Ushbu elon %d kishidan %d kishiga muvaffaqiyatli yuborildi".formatted(allUsersCount, count));
        start(user);
    }

    public void getChatId(User user, String text) {
        try {
            if (text.equals("\uD83D\uDD19 Orqaga qaytish")) {
                start(user);
                return;
            }
            Long chatId = Long.parseLong(text);
            ResponseDto<User> checkUser = userService.checkUser(chatId);
            if (!checkUser.isSuccess()) {
                bot.sendMessage(user.getChatId(), "Bunday chat id ga ega foydalanuvchi topilmadi", kyb.back);
                return;
            }
            user.setUserId(checkUser.getData().getId());
            User user1 = checkUser.getData();
            user.setUserId(user1.getId());
            userService.save(user);
            String msg = """
                    Ushbu foydalanuvchilarning malumotlari
                    
                    Telegramdagi nik: %s
                    Telegram username: %s
                    Foydalanuvchining chat id si: %d
                    Holati: %s
                    """.formatted(user1.getFirstname() + (user1.getLastname() == null ? "" : " " + user1.getLastname()), user1.getUsername() == null ? "Mavjud emas" : "@" +
                    user1.getUsername(), user1.getChatId(), (user1.getRole()).equals("user_active") ? "Faol" : (user1.getRole().equals("driver") ? "Haydovchi" : (
                    user1.getRole().equals("loader") ? "Yukchi" : "Admin")));
            bot.sendMessage(user.getChatId(), msg, kyb.updateUser(user1.getRole().equals("admin")));
        } catch (Exception e) {
            bot.sendMessage(user.getChatId(), "Iltimos, chat id ni faqat son korinishida yuboring");
        }
    }

    public void getChatId(User user, String data, CallbackQuery callbackQuery, Integer messageId, int size) {
        Integer page = user.getPage();
        Long chatId = user.getChatId();
        if (data.equals("back1")) {
            bot.deleteMessage(chatId, messageId);
            start(user);
        } else if (data.equals("admin")) {
            ResponseDto<User> userDto = userService.findById(user.getUserId());
            if (userDto.isSuccess()) {
                User data1 = userDto.getData();
                data1.setPage(0);
                data1.setEventCode("menu");
                data1.setRole("admin");
                userService.save(data1);
                bot.sendMessage(data1.getChatId(), "Tabriklaymiz. Siz admin qilindingiz. Botni ishlatish uchun /start tugmasini bosing", true);
                String msg = """
                        Ushbu foydalanuvchilarning malumotlari
                        
                        Telegramdagi nik: %s
                        Telegram username: %s
                        Foydalanuvchining chat id si: %d
                        Holati: %s
                        """.formatted(data1.getFirstname() + (data1.getLastname() == null ? "" :
                                " " + data1.getLastname()), data1.getUsername() == null ? "Mavjud emas" :
                                "@" + data1.getUsername(), data1.getChatId(),
                        data1.getRole().equals("block") ? "Bloklangan" :
                                (data1.getRole()).equals("user_active") ? "Faol" : (data1.getRole().equals("driver") ? "Haydovchi" : (
                                        data1.getRole().equals("loader") ? "Yukchi" : "Admin")

                                ));
                bot.editMessageText(chatId, messageId, msg, kyb.updateUser(data1.getRole().equals("admin")));
                bot.alertMessage(callbackQuery, "Operatsiya muvaffaqiyatli yakunlandi");
            } else {
                bot.alertMessage(callbackQuery, "Kutilmagan xatolik");
            }
            return;
        } else if (data.equals("user_block")) {
            ResponseDto<User> userDto = userService.findById(user.getUserId());
            if (userDto.isSuccess()) {
                User data1 = userDto.getData();
                data1.setPage(0);
                data1.setEventCode("menu");
                data1.setRole("user_block");
                userService.save(data1);
                bot.sendMessage(data1.getChatId(), "Siz adminlikdan olindingiz. Botni ishlatish uchun /start tugmasini bosing", true);
                String msg = """
                        Ushbu foydalanuvchilarning malumotlari
                        
                        Telegramdagi nik: %s
                        Telegram username: %s
                        Foydalanuvchining chat id si: %d
                        Holati: %s
                        """.formatted(data1.getFirstname() + (data1.getLastname() == null ? "" : " " + data1.getLastname()),
                        data1.getUsername() == null ? "Mavjud emas" : "@" + data1.getUsername(), user.getChatId(),
                        (data1.getRole()).equals("user_active") ? "Faol" : (data1.getRole().equals("driver") ? "Haydovchi" : (
                                data1.getRole().equals("loader") ? "Yukchi" : "Admin")));
                bot.editMessageText(chatId, messageId, msg, kyb.updateUser(data1.getRole().equals("admin")));
                bot.alertMessage(callbackQuery, "Operatsiya muvaffaqiyatli yakunlandi");
            } else {
                bot.alertMessage(callbackQuery, "Kutilmagan xatolik");
            }
            return;
        }
    }
}
