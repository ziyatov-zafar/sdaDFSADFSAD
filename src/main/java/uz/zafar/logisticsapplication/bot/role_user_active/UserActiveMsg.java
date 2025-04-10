package uz.zafar.logisticsapplication.bot.role_user_active;

import org.springframework.stereotype.Controller;

@Controller
public class UserActiveMsg {
    public String requestLang(String nickname) {
        return """
                Assalomu aleykum, <b>%s</b> botdan foydalanish uchun o'zingizga kerakli tilni tanlang
                
                Здравствуйте! Пожалуйста, выберите предпочитаемый язык для использования бота <b>%s</b>.""".formatted(nickname, nickname);
    }

    public String chooseLang(String lang) {
        if (lang.equals("uz"))
            return "Siz o'zbek tilini tanladingiz";
        else return "Вы выбрали русский";
    }

    public String requestContactBtn(String lang) {
        return lang.equals("uz") ? "\uD83D\uDCDE Ro'yxatdan o'tish" : "\uD83D\uDCDE Регистрация";
    }

    public String wrongBtn(String lang) {
        return lang.equals("uz") ? "❌ Iltimos, tugmalardan foydalaning" : "❌ Пожалуйста, используйте кнопки";
    }

    public String requestContact(String lang) {
        return lang.equals("uz") ? "Botdan foydalanish uchun ro'yxatdan o'tishingiz kerak" : "Для использования бота необходимо зарегистрироваться";
    }

    public String menu(String lang) {
        return lang.equals("uz") ? "Sizga quyidagilardan qaysi bo'lim kerak" : "Какой из следующих разделов вам нужен?";
    }

    public String wrongUser(String lang) {
        return lang.equals("uz") ? "Afsuski siz admin tomonidan bloklangansiz" : "К сожалению, вы были заблокированы администратором";
    }

    public String checkRole(String lang, String role) {
        if (role.equals("driver")) {
            if (lang.equals("uz")) {
                return "Siz haqiqatdan ham haydovchimisiz";
            } else return "Вы правда водитель?";
        } else {
            if (lang.equals("uz")) {
                return "Siz haqiqatdan ham yukchimisiz";
            } else return "Ты действительно обуза";
        }
    }

    public String successRole(String lang, String s) {
        if (lang.equals("uz")) {
            return "🎉 Tabriklaymiz, siz muvaffaqiyatli %s bo‘limiga o‘tdingiz!".formatted(s);
        } else if (lang.equals("ru")) {
            return "🎉 Поздравляем, вы успешно перешли в раздел %s!".formatted(s);
        }
        return "🎉 Congratulations, you have successfully moved to the %s section!".formatted(s); // Default English message
    }

}
