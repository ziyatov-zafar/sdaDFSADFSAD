package uz.zafar.logisticsapplication.bot.role_user_active;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.zafar.logisticsapplication.bot.Kyb;

@Controller
public class UserActiveKyb extends Kyb {
    public static String[] menu(String lang){
        return new String[]{
                lang.equals("uz")?"🚚 Haydovchi":"\uD83D\uDE9A Водитель",
                lang.equals("uz")?"\uD83C\uDFCB\u200D♂ Yukchi":"\uD83C\uDFCB\u200D♂ Грузчик",
                lang.equals("uz")?"Dispetcher logistik":"Логист-диспетчер",
        };
    }

    public ReplyKeyboardMarkup menuBtn(String lang) {
        return setKeyboards(menu(lang),2);
    }
}
