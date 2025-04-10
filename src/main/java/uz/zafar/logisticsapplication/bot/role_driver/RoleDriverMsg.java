package uz.zafar.logisticsapplication.bot.role_driver;

import org.springframework.stereotype.Controller;
import uz.zafar.logisticsapplication.db.domain.Country;
import uz.zafar.logisticsapplication.db.domain.Order;
import uz.zafar.logisticsapplication.db.domain.Service;
import uz.zafar.logisticsapplication.db.domain.User;

@Controller

public class RoleDriverMsg {


    public String menuMsg(String lang) {
        return lang.equals("uz") ? "Asosiy menyudasiz" : "Вы находитесь в главном меню";
    }

    public String errorBtn(String lang) {
        if (lang.equals("uz")) {
            return "⚠️ Iltimos, tugmalardan foydalaning! ⬇️";
        } else if (lang.equals("ru")) {
            return "⚠️ Пожалуйста, используйте кнопки! ⬇️";
        }
        return "⚠️ Please, use the buttons! ⬇️"; // Default English message
    }

    public String chooseCountry(String lang) {
        if (lang.equals("uz")) {
            return "🌍 O'zingizga kerakli davlatlardan birini tanlang:";
        } else if (lang.equals("ru")) {
            return "🌍 Выберите нужную вам страну:";
        }
        return "🌍 Please choose your desired country:"; // Default English message
    }

    public String pageIsZero(String lang) {
        if (lang.equals("uz")) {
            return "ℹ️ Siz eng birinchi sahifadasiz!";
        } else if (lang.equals("ru")) {
            return "ℹ️ Вы на самой первой странице!";
        }
        return "ℹ️ You are on the very first page!"; // Default English message
    }


    public String successService(Service service, Country country, String lang) {
        if (country == null) {
            if (lang.equals("uz")) {
                return """
                        📌 Xizmat turi: %s
                        
                        ✅ Siz haqiqatdan ham ushbu xizmatimiz kerakmi?
                        """.formatted(service.getNameUz());
            } else if (lang.equals("ru")) {
                return """
                        📌 Тип услуги: %s
                        
                        ✅ Вы действительно нуждаетесь в этой услуге?
                        """.formatted(service.getNameRu());
            }
            return """
                    📌 Service type: %s
                    
                    ✅ Do you really need this service?
                    """.formatted(service.getNameUz()); // Default English message

        }
        if (lang.equals("uz")) {
            return """
                    📌 Xizmat turi: %s
                    🌍 Qaysi davlatga: %s
                    
                    ✅ Siz haqiqatdan ham ushbu xizmatimiz kerakmi?
                    """.formatted(service.getNameUz(), country.getNameUz());
        } else if (lang.equals("ru")) {
            return """
                    📌 Тип услуги: %s
                    🌍 В какую страну: %s
                    
                    ✅ Вы действительно нуждаетесь в этой услуге?
                    """.formatted(service.getNameRu(), country.getNameRu());
        }
        return """
                📌 Service type: %s
                🌍 Which country: %s
                
                ✅ Do you really need this service?
                """.formatted(service.getNameUz(), country.getNameUz()); // Default English message
    }

    public String sendAdminMsg(Long orderId, String lang, Service service, Country country, String phones, User user) {
        boolean isUz = lang.equals("uz");

        if (country == null) {
            return (isUz ? "🇺🇿 O'zbek" : "🇷🇺 Русский") + """
                
                🚖 SHOFYOR
                🆔 ID: %d
                📌 Xizmat turi: %s
                📞 Foydalanuvchining telefon raqami: %s
                
                👤 Foydalanuvchi ma'lumotlari:
                🆔 ID: %d
                💬 Chat ID: %d
                🔖 Nickname: %s
                🔗 Username: %s
                """.formatted(orderId,
                    isUz ? service.getNameUz() : service.getNameRu(),
                    phones,
                    user.getId(),
                    user.getChatId(),
                    user.getNickname(),
                    (user.getUsername() == null || user.getUsername().isEmpty()) ? "Mavjud emas" : "@" + user.getUsername()
            );

        }
        return (isUz ? "🇺🇿 O'zbek" : "🇷🇺 Русский") + """
                
                🚖 SHOFYOR
                🆔 ID: %d
                📌 Xizmat turi: %s
                🌍 Davlat: %s
                📞 Foydalanuvchining telefon raqami: %s
                
                👤 Foydalanuvchi ma'lumotlari:
                🆔 ID: %d
                💬 Chat ID: %d
                🔖 Nickname: %s
                🔗 Username: %s
                """.formatted(orderId,
                isUz ? service.getNameUz() : service.getNameRu(),
                isUz ? country.getNameUz() : country.getNameRu(),
                phones,
                user.getId(),
                user.getChatId(),
                user.getNickname(),
                (user.getUsername() == null || user.getUsername().isEmpty()) ? "Mavjud emas" : "@" + user.getUsername()
        );
    }


    public String adminWithContact(String lang, String phones) {
        if (lang.equals("uz")) {
            return "📞 Admin bilan bog‘lanish uchun quyidagi raqamga qo‘ng‘iroq qilishingiz mumkin:\n\n" + phones;
        } else if (lang.equals("ru")) {
            return "📞 Для связи с администратором вы можете позвонить по следующему номеру:\n\n" + phones;
        }
        return "📞 To contact the admin, you can call the following number:\n\n" + phones; // Default English message
    }

    public String pageIsLast(String lang) {
        if (lang.equals("uz")) {
            return "ℹ️ Siz eng oxirgi sahifadasiz!";
        } else if (lang.equals("ru")) {
            return "ℹ️ Вы на самой последней странице!";
        }
        return "ℹ️ You are on the very last page!"; // Default English message
    }

    public String requestFullname(String lang) {
        if (lang.equals("uz")) {
            return "✍️ To‘liq ismingizni kiriting:";
        } else if (lang.equals("ru")) {
            return "✍️ Введите ваше полное имя:";
        }
        return "✍️ Please enter your full name:"; // Default English message
    }

    public String requestPhone(String lang) {
        if (lang.equals("uz")) {
            return "📞 Ishlaydigan telefon raqamingizni qoldiring:";
        } else if (lang.equals("ru")) {
            return "📞 Оставьте свой рабочий номер телефона:";
        }
        return "📞 Please leave your working phone number:"; // Default English message
    }

    public String orderInformation(String lang, Order order, Country country, Service service) {
        String template;
        if (country == null) {

            if (lang.equals("uz")) {
                template = """
                    🆔 Id: %d
                    📝 Sizning to‘liq ismingiz: %s
                    📞 Telefon raqamingiz: %s
                    🛠 Siz tanlagan xizmat turi: %s
                    
                    ✅ Buyurtmangiz qabul qilindi!
                    """;
                return template.formatted(order.getId(), order.getFullName(), order.getPhone(), service.getNameUz());

            } else if (lang.equals("ru")) {
                template = """
                    🆔 Id: %d
                    📝 Ваше полное имя: %s
                    📞 Ваш номер телефона: %s
                    🛠 Выбранный тип услуги: %s
                    
                    ✅ Ваш заказ принят!
                    """;
                return template.formatted(order.getId(), order.getFullName(), order.getPhone(),  service.getNameRu());
            }

            // Default English message
            template = """
                🆔 Id: %d
                📝 Your full name: %s
                📞 Your phone number: %s
                🛠 Selected service type: %s
                
                ✅ Your order has been received!
                """;
            return template.formatted(order.getId(), order.getFullName(), order.getPhone(), service.getNameUz());

        }
        if (lang.equals("uz")) {
            template = """
                    🆔 Id: %d
                    📝 Sizning to‘liq ismingiz: %s
                    📞 Telefon raqamingiz: %s
                    🌍 Siz tanlagan davlat: %s
                    🛠 Siz tanlagan xizmat turi: %s
                    
                    ✅ Buyurtmangiz qabul qilindi!
                    """;
            return template.formatted(order.getId(), order.getFullName(), order.getPhone(), country.getNameUz(), service.getNameUz());

        } else if (lang.equals("ru")) {
            template = """
                    🆔 Id: %d
                    📝 Ваше полное имя: %s
                    📞 Ваш номер телефона: %s
                    🌍 Выбранная страна: %s
                    🛠 Выбранный тип услуги: %s
                    
                    ✅ Ваш заказ принят!
                    """;
            return template.formatted(order.getId(), order.getFullName(), order.getPhone(), country.getNameRu(), service.getNameRu());
        }

        // Default English message
        template = """
                🆔 Id: %d
                📝 Your full name: %s
                📞 Your phone number: %s
                🌍 Selected country: %s
                🛠 Selected service type: %s
                
                ✅ Your order has been received!
                """;
        return template.formatted(order.getId(), order.getFullName(), order.getPhone(), country.getNameUz(), service.getNameUz());
    }


    public String successDelete(String lang) {
        if (lang.equals("uz")) {
            return "✅ <b>Muvaffaqiyatli o‘chirildi!</b>";
        } else if (lang.equals("ru")) {
            return "✅ <b>Успешно удалено!</b>";
        }
        return "✅ <b>Successfully deleted!</b>"; // Default English message
    }

}
