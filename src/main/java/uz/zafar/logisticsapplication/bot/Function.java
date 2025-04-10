package uz.zafar.logisticsapplication.bot;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.service.UserService;

public class Function {
    public void statistika(UserService userService, TelegramBot bot, Long chatId, ReplyKeyboardMarkup markup) {
        long activeUsers = 0, driverUsers = 0, loaderUsers = 0, admins = 0, blockedUsers = 0, totalUsers = 0;

        for (User user1 : userService.findAll().getData()) {
            totalUsers++;
            switch (user1.getRole()) {
                case "admin" -> admins++;
                case "user_active" -> activeUsers++;
                case "driver" -> driverUsers++;
                case "loader" -> loaderUsers++;
                case "block" -> blockedUsers++;
            }
        }
        String message = """
                📊 <b>Foydalanuvchilar statistikasi:</b>
                
                👥 Umumiy foydalanuvchilar: %d ta
                ✅ Aktiv foydalanuvchilar: %d ta
                ⛔ Bloklangan foydalanuvchilar: %d ta
                👑 Administratorlar: %d ta
                🚚 Haydovchilar: %d ta
                🏋‍♂ Yukchilar: %d ta
                """.formatted(totalUsers, activeUsers, blockedUsers, admins, driverUsers, loaderUsers);

        bot.sendMessage(chatId, message, markup);

    }



    public boolean isFinished(User user , TelegramBot bot) {
        return !bot.getChannels(user.getChatId()).isEmpty();
    }

    public boolean success(CallbackQuery callbackQuery, User user,TelegramBot bot,Kyb kyb) {
        if (user.getLang() == null) user.setLang("uz");
        if (isFinished(user, bot)) {
            bot.alertMessage(callbackQuery, user.getLang().equals("ru") ? "⚠️ Вам необходимо подписаться на каналы ниже." : "⚠️ Quyidagi kanallarga to'liq azo bolishingiz kerak");
            bot.editMessageText(user.getChatId(), callbackQuery.getMessage().getMessageId(), "Quyidagi kanallarga a'zo bo'ling", kyb.subscribeChannel(bot.getChannels(user.getChatId()), user.getLang()));
            return false;
        } else {
            bot.alertMessage(callbackQuery, user.getLang().equals("ru") ? "✅ Вы полностью зависимы от каналов" : "✅ Siz kanallarga to'liq azo boldingiz");
            bot.deleteMessage(user.getChatId(), callbackQuery.getMessage().getMessageId());
            return true;
        }
    }

    public void updateChannel(User user,TelegramBot bot,Kyb kyb) {
        bot.sendMessage(user.getChatId(), "Quyidagi kanallarga to'liq azo boling", kyb.subscribeChannel(bot.getChannels(user.getChatId()), user.getLang()));
    }
    public boolean inArray(String str, String[] arr) {
        for (String s : arr) {
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }
    public boolean inArray(String str,  String s1) {
        return inArray(str , new String[]{s1});
    }
    public boolean inArray(String str,  String s1,String s2) {
        return inArray(str , new String[]{s1,s2});
    }
    public boolean inArray(String str,  String s1,  String s2,  String s3) {
        return inArray(str , new String[]{s1,s2,s3});
    }
    public boolean inArray(String str,  String s1,String s2 , String s3 , String s4) {
        return inArray(str , new String[]{s1,s2,s3,s4});
    }
    public boolean inArray(String str,  String s1,  String s2 , String s3 , String s4 , String s5) {
        return inArray(str , new String[]{s1,s2,s3,s4,s5});
    }
    public String[]arr(String s1 , String s2){
        return new String[]{s1,s2};
    }
    public String[]arr(String s1 ){
        return new String[]{s1};
    }
    public String[]arr(String s1,String s2,String s3 ){
        return new String[]{s1,s2,s3};
    }
    public String[]arr(String s1,String s2,String s3,String s4 ){
        return new String[]{s1,s2,s3,s4};
    }
    public String[]arr(String s1,String s2,String s3,String s4,String s5 ){
        return new String[]{s1,s2,s3,s4,s5};
    }
}
