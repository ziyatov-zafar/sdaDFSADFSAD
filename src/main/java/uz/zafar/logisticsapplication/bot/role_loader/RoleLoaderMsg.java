package uz.zafar.logisticsapplication.bot.role_loader;

import org.springframework.stereotype.Controller;
import uz.zafar.logisticsapplication.db.domain.Load;
import uz.zafar.logisticsapplication.db.domain.User;

import java.util.List;

@Controller
public class RoleLoaderMsg {
    public String menu(String lang) {
        if ("uz".equals(lang)) {
            return "📌 Asosiy menyudasiz, o'zingizga kerakli bo'limlardan birini tanlang.";
        } else {
            return "📌 Вы находитесь в главном меню, выберите нужный вам раздел.";
        }
    }

    public String addLoad(String lang) {
        if ("uz".equals(lang)) {
            return "📝 To‘liq ismingizni kiriting:";
        } else {
            return "📝 Введите ваше полное имя:";
        }
    }

    public String notFoundLoad(String lang) {
        if ("uz".equals(lang)) {
            return "❌ Sizda yuklar mavjud emas.";
        } else {
            return "❌ У вас нет грузов.";
        }
    }


    public static String loadInformation(String lang, Load load) {
        if (load == null) {
            return "<b>⚠️ Ma'lumot topilmadi!</b>\n<b>⚠️ Данные не найдены!</b>";
        }

        String info;

        if ("uz".equals(lang)) {
            info = "<b>📦 Yuk ma'lumotlari:</b>\n" +
                    "<b>🆔 ID:</b> <code>" + load.getId() + "</code>\n" +
                    "<b>📍 Qayerdan Qayerga:</b> <i>" + load.getToAddressAndFromAddress() + "</i>\n" +
                    "<b>📌 Nomi:</b> <code>" + load.getName() + "</code>\n" +
                    "<b>⚖ Og'irligi:</b> <code>" + load.getWeight() + " </code>\n" +
                    "<b>💰 Narxi:</b> <code>" + load.getPrice() + "</code>\n" +
                    "<b>🚛 Mashina soni:</b> <code>" + load.getCarCount() + "</code>\n" +
                    "<b>👤 Mas'ul shaxs:</b> <code>" + load.getFullName() + "</code>\n" +
                    "<b>💵 Avans :</b> <code>" + (load.getIsAdvance() ? "Bor ("+load.getAdvance() + ")" : "Yo‘q") +"</code>\n" +
                    "<b>💳 To‘lov turi:</b> <code>" + load.getPaymentType() + "</code>\n" +
                    "<b>📞 Telefon:</b> <code>" + load.getPhone() + "</code>\n" ;
        } else if ("ru".equals(lang)) {
            info = "<b>📦 Информация о грузе:</b>\n" +
                    "<b>🆔 ID:</b> <code>" + load.getId() + "</code>\n" +
                    "<b>📍 Откуда Куда:</b> <i>" + load.getToAddressAndFromAddress() + "</i>\n" +
                    "<b>📌 Название:</b> <code>" + load.getName() + "</code>\n" +
                    "<b>⚖ Вес:</b> <code>" + load.getWeight() + " </code>\n" +
                    "<b>💰 Цена:</b> <code>" + load.getPrice() + "</code>\n" +
                    "<b>🚛 Количество машин:</b> <code>" + load.getCarCount() + "</code>\n" +
                    "<b>👤 Ответственное лицо:</b> <code>" + load.getFullName() + "</code>\n" +
                    "<b>💵 Аванс:</b> <code>" + (load.getIsAdvance() ? "Есть" : "Нет") + " (" + load.getAdvance() + ")</code>\n" +
                    "<b>💳 Тип оплаты:</b> <code>" + load.getPaymentType() + "</code>\n" +
                    "<b>📞 Телефон:</b> <code>" + load.getPhone() + "</code>\n" ;
        } else {
            info = "<b>⚠️ Til topilmadi! / Язык не найден!</b>";
        }

        return info;
    }


    public String wrongBtn(String lang) {
        if ("uz".equals(lang)) {
            return "⚠️ Noto‘g‘ri tugma bosildi.\n\n🔹 Iltimos, menyudan tegishli tugmani tanlang ";
        } else {
            return "⚠️ Нажата неверная кнопка.\n\n🔹 Пожалуйста, выберите соответствующую кнопку в меню";
        }
    }

    public String successDelete(String lang) {
        if ("uz".equals(lang)) {
            return "✅ Muvaffaqiyatli o‘chirildi!";
        } else {
            return "✅ Успешно удалено!";
        }
    }

    public String noDelete(String lang) {
        if ("uz".equals(lang)) {
            return "❌ O‘chirilmadi!";
        } else {
            return "❌ Не удалено!";
        }
    }


    public String requestDelete(String lang) {
        if ("uz".equals(lang)) {
            return "❗ Ushbu yuk haqiqatdan ham o‘chirilsinmi?";
        } else {
            return "❗ Вы действительно хотите удалить этот груз?";
        }
    }

    public String changeLang(String lang) {
        if (lang.equals("uz")) {
            return "🇺🇿 Til o'zgartirildi ✅";
        } else {
            return "🇷🇺 Язык изменён ✅";
        }
    }

    public String toAddressAndFromAddress(String lang) {
        return lang.equals("uz")
                ? "📍 Yukingiz qayerdan qayerga olib boriladi?"
                : "📍 Откуда и куда будет доставлен ваш груз?";
    }

    public String requestLoadName(String lang) {
        if ("uz".equals(lang)) {
            return "📦 Yuk nomini kiriting:";
        } else {
            return "📦 Введите название груза:";
        }
    }

    public String requestLoadWeight(String lang) {
        if ("uz".equals(lang)) {
            return "⚖️ Yuk og'irligini kiriting:";
        } else {
            return "⚖️ Введите вес груза:";
        }
    }


    public String requestLoadPrice(String lang) {
        if ("uz".equals(lang)) {
            return "💰 Yuk narxini kiriting:";
        } else {
            return "💰 Введите цену груза:";
        }
    }

    public String requestLoadCarCount(String lang) {
        if ("uz".equals(lang)) {
            return "🚛 Mashinalar sonini kiriting:";
        } else {
            return "🚛 Введите количество машин:";
        }
    }

    public String requestLoadIsAdvance(String lang) {
        if ("uz".equals(lang)) {
            return "💵 Avans bormi?";
        } else {
            return "💵 Есть ли аванс?";
        }
    }

    public String errorCarCount(String lang) {
        if ("uz".equals(lang)) {
            return "⚠️ Iltimos, mashinalar sonini faqat sonlarda kiriting!";
        } else {
            return "⚠️ Пожалуйста, введите количество машин цифрами!";
        }
    }

    public String getAdvancePrice(String lang) {
        if ("uz".equals(lang)) {
            return "💰 Avans narxini kiriting:";
        } else {
            return "💰 Введите сумму аванса:";
        }
    }

    public String paymentType(String lang) {
        if ("uz".equals(lang)) {
            return "💳 To'lov turini kiriting:";
        } else {
            return "💳 Введите тип оплаты:";
        }
    }

    public String getPhone(String lang) {
        if ("uz".equals(lang)) {
            return "📞 Siz bilan bog'lanish uchun telefon raqamingizni kiriting:";
        } else {
            return "📞 Введите ваш номер телефона для связи:";
        }
    }

    public String isTrue(String lang) {
        if ("uz".equals(lang)) {
            return "Ushbu yukni qo'shmoqchimisiz?";
        } else {
            return "Вы хотите добавить этот груз?";
        }
    }

    public String successAddLoad(String lang) {
        if ("uz".equals(lang)) {
            return "✅ Ushbu yuk muvaffaqiyatli qo'shildi!";
        } else {
            return "✅ Этот груз успешно добавлен!";
        }
    }
    public String notAddLoad(String lang) {
        if ("uz".equals(lang)) {
            return "❌ Ushbu yuk qo'shilmadi!";
        } else {
            return "❌ Этот груз не был добавлен!";
        }
    }

    public String sendMsgToAdmin(String lang, List<User> admins) {
        List<String> phones = admins.stream().map(User::getHelperPhone).toList();
        StringBuilder phoneNumbers = new StringBuilder();

        for (String phone : phones) {
            phoneNumbers.append(phone).append(" ");
        }

        if (lang.equals("uz")) {
            return """
                Admin bilan bog'lanish uchun quyidagi telefon raqamlariga qo'ng'iroq qilishingiz mumkin:
                
                %s
                """.formatted(phoneNumbers.toString().trim());
        } else if (lang.equals("ru")) {
            return """
                Для связи с администратором, вы можете позвонить по следующим номерам телефонов:
                
                %s
                """.formatted(phoneNumbers.toString().trim());
        } else {
            return "Language not supported";
        }
    }
}
