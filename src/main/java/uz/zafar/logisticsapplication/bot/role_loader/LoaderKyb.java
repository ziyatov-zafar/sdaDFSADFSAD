package uz.zafar.logisticsapplication.bot.role_loader;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.zafar.logisticsapplication.bot.Kyb;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoaderKyb extends Kyb {
    public ReplyKeyboardMarkup menu(String lang) {
        if ("uz".equals(lang)) {
            return setKeyboards(new String[]{
                    "➕ Yuk qo‘shish",
                    "📦 Mening yuklarim",
                    "⚙️ Tilni o‘zgartirish"
            }, 1);
        } else {
            return setKeyboards(new String[]{
                    "➕ Добавить груз",
                    "📦 Мои грузы",
                    "⚙️ Изменить язык"
            }, 1);
        }
    }


    public ReplyKeyboardMarkup back(String lang) {
        if ("uz".equals(lang)) {
            return setKeyboards(new String[]{"⬅️ Orqaga"}, 1);
        } else {
            return setKeyboards(new String[]{"⬅️ Назад"}, 1);
        }
    }

    public InlineKeyboardMarkup deleteBtn(String lang, Long id) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        String buttonText = "🗑️ " + ("uz".equals(lang) ? "O‘chirish" : "Удалить");

        row.add(createButton(buttonText, id));
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }


}
