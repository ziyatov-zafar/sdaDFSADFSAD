package uz.zafar.logisticsapplication.bot.role_super_admin;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.zafar.logisticsapplication.bot.Kyb;
import uz.zafar.logisticsapplication.db.domain.User;

import java.util.ArrayList;
import java.util.List;


@Controller
@Log4j2
public class SuperAdminKyb extends Kyb {
    private String backButton = "\uD83D\uDD19 Orqaga qaytish";
    public ReplyKeyboardMarkup back = setKeyboards(new String[]{backButton}, 1);
    public ReplyKeyboardMarkup menu = menu();

    private ReplyKeyboardMarkup menu() {
        String[] menu = {
                "Foydalanuvchilar bo'limi", "Reklama bo'limi", "Statistika bo'limi", "Foydalanuvchilarni chat id bo'yicha qidirish"
        };
        KeyboardButton button = new KeyboardButton();
        button.setText(menu[0]);
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        button = new KeyboardButton();
        button.setText(menu[1]);
        row.add(button);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        button = new KeyboardButton();
        row = new KeyboardRow();
        button.setText(menu[2]);
        row.add(button);
        rows.add(row);
        row = new KeyboardRow();
        button = new KeyboardButton();
        button.setText(menu[3]);
        row.add(button);
        rows.add(row);
        return new ReplyKeyboardMarkup(rows, true, true, true, "O'zingizga kerakli menyulardan birini tanlang", false);
    }



    public InlineKeyboardMarkup updateUser(boolean admin) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(admin ? "Adminlikdan olish" : "Admin qilish");
        button.setCallbackData(admin ? "user_active" : "admin");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(backButton);
        button.setCallbackData("back1");
        row.add(button);
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }
}
