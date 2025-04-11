package uz.zafar.logisticsapplication.bot.role_driver;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.logisticsapplication.bot.Kyb;
import uz.zafar.logisticsapplication.db.domain.Service;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DriverKyb extends Kyb {

    public ReplyKeyboardMarkup menu(List<Service> list, String lang, Boolean presentAddress) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow addressRow = new KeyboardRow();
        if (presentAddress) {
            addressRow.add(
                    lang.equals("uz") ? "📍 Manzilni o'zgartirish" : "📍 Изменить адрес"
            );
        } else {
            addressRow.add(
                    lang.equals("uz") ? "➕ Manzil qo'shish" : "➕ Добавить адрес"
            );
        }

        rows.add(addressRow);
        // Xizmatlarni qo‘shish (2 ta per qator)
        for (Service service : list) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(lang.equals("uz") ? service.getNameUz() : service.getNameRu()));
            rows.add(row);
        }

        // "Mening buyurtmalarim" va "Tilni o‘zgartirish" tugmalarini bitta qatorga joylash
        KeyboardRow additionalRow = new KeyboardRow();
        if (lang.equals("uz")) {
            additionalRow.add(new KeyboardButton("📋 Mening buyurtmalarim"));
            additionalRow.add(new KeyboardButton("🌐 Tilni o‘zgartirish"));
        } else {
            additionalRow.add(new KeyboardButton("📋 Мои заказы"));
            additionalRow.add(new KeyboardButton("🌐 Изменить язык"));
        }
        rows.add(additionalRow);

        // ReplyKeyboardMarkup yasash
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);

        return markup;
    }


    public ReplyKeyboardMarkup back(String lang) {
        return setKeyboards(
                new String[]{lang.equals("ru") ? "⬅️ Назад" : "⬅️ Orqaga"},
                1
        );
    }


    public InlineKeyboardMarkup deleteOrder(Long id, String lang) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Yangi ikonka bilan "O'chirish" tugmasi
        String buttonText = lang.equals("uz") ? "❌ O‘chirish" : "❌  Удалить";

        // ID ni String formatiga o'tkazish
        row.add(createButton(buttonText, String.valueOf(id)));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

}
