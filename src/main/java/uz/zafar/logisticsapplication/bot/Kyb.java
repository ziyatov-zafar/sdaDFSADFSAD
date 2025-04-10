package uz.zafar.logisticsapplication.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.logisticsapplication.db.domain.Channel;
import uz.zafar.logisticsapplication.db.domain.Country;
import uz.zafar.logisticsapplication.db.domain.User;

import java.util.ArrayList;
import java.util.List;

import static uz.zafar.logisticsapplication.bot.StaticVariables.backButton;

public class Kyb {
    public ReplyKeyboardMarkup back = setKeyboards(new String[]{backButton}, 1);

    public ReplyKeyboardMarkup markup(List<KeyboardRow> r) {
        return ReplyKeyboardMarkup.builder().selective(true).resizeKeyboard(true).keyboard(r).build();
    }

    public ReplyKeyboardMarkup requestLang = setKeyboards(new String[]{"\uD83C\uDDFA\uD83C\uDDFF O'zbek tili", "\uD83C\uDDF7\uD83C\uDDFA Русский язык"}, 1);
    public InlineKeyboardMarkup successOrder(Long orderId) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("✅ Qabul qilish", "successorder_" + orderId));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }
    public InlineKeyboardMarkup addLoadForAdmin(Long id) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("✅ Qabul qilindi", id));
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }
    public ReplyKeyboardMarkup setKeyboards(String[] words, int size) {
        KeyboardButton button;

        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            button = new KeyboardButton();
            button.setText(words[i]);
            row.add(button);
            if ((i + 1) % size == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }

    public ReplyKeyboardMarkup requestContact(String word) {
        KeyboardButton button;
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        button = new KeyboardButton();
        button.setText(word);
        button.setRequestContact(true);
        row.add(button);
        rows.add(row);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setKeyboard(rows);
        return markup;
    }


    public InlineKeyboardButton createButton(String text, String data) {
        return InlineKeyboardButton.builder().callbackData(data).text(text).build();
    }

    public InlineKeyboardButton createButton(String text, Long data) {
        return InlineKeyboardButton.builder().callbackData(String.valueOf(data)).text(text).build();
    }

    public InlineKeyboardMarkup subscribeChannel(List<Channel> channels, String lang) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Channel channel : channels) {
            button.setText(channel.getName());
            button.setUrl(channel.getLink());
            row.add(button);
            rows.add(row);
            row = new ArrayList<>();
            button = new InlineKeyboardButton();
        }
        button = new InlineKeyboardButton();
        if (lang == null) lang = "uz";
        button.setText(lang.equals("ru") ? "✅ Подтверждение" : "✅ Tasdiqlash");
        button.setCallbackData("success");
        row.add(button);
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup userIds(List<User> users) {
        InlineKeyboardButton button;
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1));
            button.setCallbackData(String.valueOf(users.get(i).getId()));
            row.add(button);
            if ((i + 1) % 5 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText("⬅️");
        button.setCallbackData("prev");
        row.add(button);
        button = new InlineKeyboardButton();
        button.setText("➡️");
        button.setCallbackData("next");
        row.add(button);
        rows.add(row);
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(backButton);
        button.setCallbackData("back");
        row.add(button);
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }

    public ReplyKeyboardMarkup isSuccess(String lang) {
        if (lang.equals("uz")) {
            return setKeyboards(new String[]{"✅ Ha", "❌ Yo'q"}, 2);
        } else return setKeyboards(new String[]{"«❌ Да»", "«❌ Нет»"}, 2);
    }

    public InlineKeyboardMarkup isSuccessBtn(String lang) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (lang.equals("uz")) {
            row.add(createButton("✅ Ha", "yes delete"));
            rows.add(row);
            row = new ArrayList<>();
            row.add(createButton("❌ Yo'q", "no delete"));

        } else {
            row.add(createButton("«✅ Да»", "yes delete"));
            rows.add(row);
            row = new ArrayList<>();
            row.add(createButton("«❌ Нет»", "no delete"));

        }
        rows.add(row);
        return new InlineKeyboardMarkup(rows);

    }

    public InlineKeyboardMarkup setCountries(List<Country> countries, String lang, boolean isAdmin) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Davlatlar tugmalari
        for (Country country : countries) {
            String countryName = lang.equals("ru") ? country.getNameRu() : country.getNameUz();
            List<InlineKeyboardButton> row = List.of(createButton(countryName, country.getId()));
            rows.add(row);
        }

        // Pagination tugmalari
        List<InlineKeyboardButton> paginationRow = List.of(createButton("⏪ " + (lang.equals("ru") ? "Предыдущий" : "Oldingi"), "prev"), createButton((lang.equals("ru") ? "Следующий" : "Keyingi") + " ⏩", "next"));
        List<InlineKeyboardButton> addCountryRow = new ArrayList<>();
        if (isAdmin)
            addCountryRow = List.of(createButton("➕ " + (lang.equals("ru") ? "Добавить страну" : "Davlat qo‘shish"), "add_country"));
        List<InlineKeyboardButton> deleteRow = new ArrayList<>();
        if (isAdmin)
            // O'chirish tugmasi
            deleteRow = List.of(createButton("🗑 " + (lang.equals("ru") ? "Удалить все страны" : "Barcha davlatlarni o‘chirish"), "delete_all_countries"));
        // Orqaga tugmasi
        List<InlineKeyboardButton> backRow = List.of(createButton("⬅️ " + (lang.equals("ru") ? "Назад" : "Orqaga"), "to back"));

        // Tugmalarni ro‘yxatga qo‘shish
        rows.add(paginationRow);
        rows.add(addCountryRow);
        rows.add(deleteRow);
        rows.add(backRow);

        return new InlineKeyboardMarkup(rows);
    }


}
