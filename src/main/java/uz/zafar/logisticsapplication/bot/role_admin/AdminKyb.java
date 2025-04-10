package uz.zafar.logisticsapplication.bot.role_admin;

import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.logisticsapplication.bot.Kyb;
import uz.zafar.logisticsapplication.db.domain.Channel;
import uz.zafar.logisticsapplication.db.domain.Service;

import java.util.ArrayList;
import java.util.List;

import static uz.zafar.logisticsapplication.bot.StaticVariables.*;

@Controller
public class AdminKyb extends Kyb {

    public ReplyKeyboardMarkup menu = setKeyboards(adminMenu, 2);
    public ReplyKeyboardMarkup crud = setKeyboards(new String[]{addChannel, backButton}, 1);
    public ReplyKeyboardMarkup editPhone = setKeyboards(
            new String[]{"Telefon raqamni o'zgartirish", backButton}, 1
    );
    public ReplyKeyboardMarkup addServiceBtn = setKeyboards(new String[]{"Xizmat qo'shish", backButton}, 1);

    public InlineKeyboardMarkup crudService() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Birinchi qator: Nomi o'zgartirish
        List<InlineKeyboardButton> row1 = List.of(
                createButton("🇺🇿 O'zbekcha nom", "edit name uz"),
                createButton("🇷🇺 Ruscha nom", "edit name ru")
        );

        // Ikkinchi qator: Davlatlar ro‘yxatini ko‘rish
        List<InlineKeyboardButton> row2 = List.of(
                createButton("🌍 Davlatlar ro'yxati", "seen country lists")
        );

        // Uchinchi qator: O'chirish tugmasi
        List<InlineKeyboardButton> row3 = List.of(
                createButton("❌ O'chirish", "delete")
        );

        // To‘rtinchi qator: Orqaga tugmasi
        List<InlineKeyboardButton> row4 = List.of(
                createButton("⬅️ Orqaga", "back")
        );

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        return new InlineKeyboardMarkup(rows);
    }



       /* if (phone2 == null)return setKeyboards(new String[]{
                phone1 , backButton
        }, 1);
        else return setKeyboards(new String[]{
                phone1 , phone2 , backButton
        }, 2);
*/


    public ReplyKeyboardMarkup usersPage() {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<KeyboardRow>();
        for (int i = 0; i < userPageMenu.length; i++) {
            row.add(new KeyboardButton(userPageMenu[i]));
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(new KeyboardButton(backButton));
        rows.add(row);
        return ReplyKeyboardMarkup.builder().keyboard(rows).selective(true).resizeKeyboard(true).build();
    }

    public InlineKeyboardMarkup updateUser(String role) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        switch (role) {
            case "user_active", "loader", "driver" -> {
                row.add(createButton("Bloklash", "block"));
                rows.add(row);
            }
            case "block" -> {
                row.add(createButton("Blokdan chiqarish", "open block"));
                rows.add(row);
            }
        }
        row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text(backButton).callbackData("to back").build());
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public InlineKeyboardMarkup channelIds(List<Channel> list) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            row.add(createButton(String.valueOf(i + 1), list.get(i).getId()));
            if ((i + 1) % 5 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        rows.add(row);
        row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text(backButton).callbackData("back").build());
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public InlineKeyboardMarkup channelCrud() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("Nomini o'zgartirish", "edit name"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("Id(Username) o'zgartirish", "edit channel id"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("Havolani o'zgartirish", "edit channel link"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton("O'chirish", "delete"));
        rows.add(row);
        row = new ArrayList<>();
        row.add(createButton(backButton, "back1"));
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public InlineKeyboardMarkup isSuccess() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        row.add(createButton("Ha", "yes"));
        row.add(createButton("Yo'q", "no"));
        rows.add(row);
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public ReplyKeyboardMarkup setPhoneNumbers(String phone, String helperPhone) {
        if (helperPhone != null && !phone.equals(helperPhone)) {
            return setKeyboards(new String[]{phone, helperPhone, backButton}, 1);
        }
        return setKeyboards(new String[]{phone, backButton}, 1);
    }

    public ReplyKeyboardMarkup getAllServices(List<Service> list) {
        String[] keys = new String[list.size() + 1+1];
        for (int i = 0; i < list.size(); i++) {
            keys[i] = list.get(i).getNameUz();
        }
        keys[keys.length - 1] = backButton;
        keys[keys.length - 2] = "Xizmat qo'shish";
        return setKeyboards(keys, 1);
    }

    public InlineKeyboardMarkup crudCountry( Long countryId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        String lang="uz" ;
        // Bitta davlatni o‘chirish tugmasi (specific countryId bilan)
        List<InlineKeyboardButton> deleteRow = List.of(
                createButton("🗑 " + "O‘chirish", "delete country")
        );

        // Orqaga tugmasi
        List<InlineKeyboardButton> backRow = List.of(
                createButton("⬅️ " + "Orqaga", "back1")
        );

        // Tugmalarni ro‘yxatga qo‘shish
        rows.add(deleteRow);
        rows.add(backRow);

        return new InlineKeyboardMarkup(rows);
    }

}
