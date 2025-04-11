package uz.zafar.logisticsapplication.bot.role_driver;

//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.zafar.logisticsapplication.bot.Function;
import uz.zafar.logisticsapplication.bot.TelegramBot;
import uz.zafar.logisticsapplication.db.domain.Country;
import uz.zafar.logisticsapplication.db.domain.Order;
import uz.zafar.logisticsapplication.db.domain.Service;
import uz.zafar.logisticsapplication.db.domain.User;
import uz.zafar.logisticsapplication.db.service.CountryService;
import uz.zafar.logisticsapplication.db.service.OrderService;
import uz.zafar.logisticsapplication.db.service.ServiceService;
import uz.zafar.logisticsapplication.db.service.UserService;
import uz.zafar.logisticsapplication.dto.ResponseDto;

import java.util.List;

@Controller
//@Log4j2
public class DriverFunction extends Function {

    public final TelegramBot bot;
    public final UserService userService;
    public final DriverKyb kyb;
    public final RoleDriverMsg msg;
    private final ServiceService serviceService;
    private final CountryService countryService;
    private final OrderService orderService;

    public DriverFunction(TelegramBot bot, UserService userService, DriverKyb kyb, RoleDriverMsg msg, ServiceService serviceService, CountryService countryService, OrderService orderService) {
        this.bot = bot;
        this.userService = userService;
        this.kyb = kyb;
        this.msg = msg;
        this.serviceService = serviceService;
        this.countryService = countryService;
        this.orderService = orderService;
    }

    public void start(User user) {
        List<Service> list = serviceService.findAll(user.getLang()).getData();
        bot.sendMessage(user.getChatId(), msg.menuMsg(user.getLang()), kyb.menu(list, user.getLang(), user.getAddress() != null));
        user.setEventCode("menu");
        user.setPage(0);
        userService.save(user);
    }

    public void menu(User user, String text) {
        if (inArray(text, "🌐 Изменить язык", "🌐 Tilni o‘zgartirish")) {
            if (user.getLang().equals("uz")) {
                user.setLang("ru");
            } else user.setLang("uz");
            userService.save(user);
            start(user);
        } else if (inArray(text, "⬅️ Назад", "⬅️ Orqaga")) {
            start(user);
        } else if (inArray(text, "📍 Manzilni o'zgartirish", "📍 Изменить адрес")) {
            user.setEventCode("change address");
            if (user.getLang().equals("uz")) {
                bot.sendMessage(
                        user.getChatId(),
                        "%s\n\n❗️Haqiqatdan ham yuk manzilini o‘zgartirmoqchimisiz?".formatted(
                                "Sizning avvalgi manzilingiz: <code>%s</code>.".formatted(user.getAddress())
                        ),
                        kyb.isSuccess(user.getLang())
                );
            } else if (user.getLang().equals("ru")) {
                bot.sendMessage(
                        user.getChatId(),
                        "%s\n\n❗️Вы действительно хотите изменить адрес доставки?".formatted(
                                "Ваш предыдущий адрес: <code>%s</code>.".formatted(user.getAddress())
                        ),
                        kyb.isSuccess(user.getLang())
                );
            }

            userService.save(user);
            return;
        } else if (inArray(text, "➕ Manzil qo'shish", "➕ Добавить адрес")) {
            bot.sendMessage(
                    user.getChatId(),
                    "🚚 Yuk qaysi manzildan qaysi manzilga kerakligini kiriting:",
                    kyb.back(user.getLang())
            );
            user.setEventCode("add new address");
            userService.save(user);
            return;
        } else if (inArray(text, "📋 Мои заказы", "📋 Mening buyurtmalarim")) {
            bot.sendMessage(user.getChatId(), text, kyb.back(user.getLang()));
            List<Order> list = orderService.findAllByUserId(user.getId()).getData();
            for (Order order : list) {
                Country country = countryService.findById(order.getCountryId()).getData();
                Service service = serviceService.findById(order.getServiceId()).getData();
                bot.sendMessage(user.getChatId(), msg.orderInformation(user.getLang(), order, country, service), kyb.deleteOrder(order.getId(), user.getLang()));
            }
        } else {
            Service service;
            ResponseDto<Service> checkService;
            if (user.getLang().equals("uz")) {
                checkService = serviceService.findByNameUz(text);
            } else {
                checkService = serviceService.findByNameRu(text);
            }
            if (checkService.isSuccess()) {
                service = checkService.getData();
                user.setServiceId(service.getId());
                user.setEventCode("countries menu");
                userService.save(user);
                Page<Country> countries = countryService.findAllByService(service.getId(), user.getPage(), bot.size, user.getLang()).getData();

                if (countryService.countByServiceId(service.getId()) == 0) {
                    Country country = null;
                    user.setCountryId(null);
                    userService.save(user);
                    bot.sendMessage(user.getChatId(), text, kyb.back(user.getLang()));
                    bot.sendMessage(user.getChatId(), msg.successService(service(user.getServiceId()), country, user.getLang()), kyb.isSuccessBtn(user.getLang()));
                    return;
                }
                bot.sendMessage(user.getChatId(), text, kyb.back(user.getLang()));
                bot.sendMessage(user.getChatId(), msg.chooseCountry(user.getLang()), kyb.setCountries(countries.stream().toList(), user.getLang(), false));
            } else {
                List<Service> list = serviceService.findAll(user.getLang()).getData();
                bot.sendMessage(user.getChatId(), msg.errorBtn(user.getLang()), kyb.menu(
                        list, user.getLang(), user.getAddress() != null
                ));
            }
        }
    }

    public void countriesMenu(User user, String text) {
        if (inArray(text, arr("⬅️ Назад", "⬅️ Orqaga"))) {
            start(user);
            return;
        }
        bot.sendMessage(user.getChatId(), msg.errorBtn(user.getLang()), kyb.back(user.getLang()));
    }

    public void countriesMenu(User user, Integer messageId, CallbackQuery callbackQuery, String data) {
        Integer page = user.getPage();
        int size = bot.size;

        if (data.equals("next")) {
            page = page + 1;
        } else if (data.equals("prev")) {
            if (page == 0) {
                bot.alertMessage(callbackQuery, msg.pageIsZero(user.getLang()));
                return;
            }
            page = page - 1;
        } else {
            switch (data) {
                case "to back" -> {
                    bot.deleteMessage(user.getChatId(), messageId);
                    start(user);
                }
                case "yes delete" -> {
                    bot.deleteMessage(user.getChatId(), messageId);
                    bot.sendMessage(user.getChatId(), msg.requestFullname(user.getLang()), kyb.back(user.getLang()));
                    user.setEventCode("get full name");
                    userService.save(user);

                }
                case "no delete" -> {
                    Service service = service(user.getServiceId());
                    user.setEventCode("countries menu");
                    userService.save(user);
                    Page<Country> countries = countryService.findAllByService(service.getId(), user.getPage(), bot.size, user.getLang()).getData();
                    bot.editMessageText(user.getChatId(), messageId, msg.chooseCountry(user.getLang()), kyb.setCountries(countries.stream().toList(), user.getLang(), false));
                }
                default -> {
                    Country country = countryService.findById(Long.valueOf(data)).getData();
                    user.setCountryId(country.getId());
                    userService.save(user);
                    bot.editMessageText(user.getChatId(), messageId, msg.successService(service(user.getServiceId()), country, user.getLang()), kyb.isSuccessBtn(user.getLang()));

                }
            }
            return;
        }

        user.setPage(page);
        Page<Country> countryPage = countryService.findAllByService(service(user.getServiceId()).getId(), user.getPage(), bot.size, "uz").getData();
        boolean hasCountries = !countryPage.getContent().isEmpty();
        if (!hasCountries) {
            bot.alertMessage(callbackQuery, msg.pageIsLast(user.getLang()));
            return;
        }
        userService.save(user);

        bot.editMessageText(user.getChatId(), messageId,
                msg.chooseCountry(user.getLang()),
                kyb.setCountries(countryPage.getContent(), user.getLang(), false));
    }

    private Service service(Long id) {
        return serviceService.findById(id).getData();
    }

    private void sendContact(Long chatId, String phone, String firstName, String lastName) {
        try {
            SendContact sendContact = SendContact
                    .builder()
                    .chatId(chatId)
                    .phoneNumber(phone)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build();
            bot.execute(sendContact);
        } catch (TelegramApiException e) {

        }
    }

    public void addOrder(User user, String text, String eventCode) {
        ResponseDto<Order> checkOrder = orderService.findByStatus(user.getId(), "draft");
        Order order;
        if (checkOrder.isSuccess()) order = checkOrder.getData();
        else {
            order = new Order();
            order.setUserId(user.getId());
            order.setActive(false);
            order.setCountryId(user.getCountryId());
            order.setServiceId(user.getServiceId());
            order.setStatus("draft");
        }
        if (eventCode.equals("get full name")) {
            order.setFullName(text);
            orderService.save(order);
            bot.sendMessage(user.getChatId(), msg.requestPhone(user.getLang()), true);
            user.setEventCode("get phone number");
            userService.save(user);
        } else if (eventCode.equals("get phone number")) {
            order.setPhone(text);
            order.setStatus("open");
            order.setActive(true);
            orderService.save(order);
            /*bot.sendMessage(user.getChatId(), msg.requestPhone(user.getLang()), true);*/
            user.setEventCode("get phone number");
            userService.save(user);
            Country country = countryService.findById(order.getCountryId()).getData();
            Service service = service(order.getServiceId());
            bot.sendMessage(user.getChatId(), msg.orderInformation(
                    user.getLang(), order, country, service
            ));

            ///     ////////////////////////////////////////////////////////////

            String s = "";
            List<User> list = userService.findAllByRole("admin", 0, 100).getData().getContent();
            for (int i = 0; i < list.size(); i++) {
                s = s.concat("\t\t%d) %s\n".formatted(i + 1, list.get(i).getHelperPhone()));
            }
            bot.sendMessage(user.getChatId(), msg.adminWithContact(user.getLang(), s));
            for (User admin : list) {
                sendContact(user.getChatId(), admin.getPhone(), admin.getFirstname(), admin.getLastname());
            }
            String p = user.getPhone();
            if (user.getHelperPhone() != null) {
                p = p.concat(", " + user.getHelperPhone());
            }
            p = p.concat(", " + order.getPhone());
            for (User admin : list) {
                bot.sendMessage(admin.getChatId(), msg.sendAdminMsg(order.getId(), user.getLang(), service(user.getServiceId()), country, p, user), kyb.successOrder(order.getId()));
                admin.setEventCode("menu");
                userService.save(admin);
            }
            for (User admin : list) {
                sendContact(admin.getChatId(), user.getPhone(), user.getFirstname(), user.getLastname());
            }
            start(user);

        }
    }

    public void menu(User user, CallbackQuery callbackQuery, Integer messageId, String data) {
        Long orderId = Long.valueOf(data);
        Order order = orderService.findById(orderId).getData();
        order.setActive(false);
        orderService.save(order);

        bot.alertMessage(callbackQuery, msg.successDelete(user.getLang()));
        bot.deleteMessage(user.getChatId(), messageId);
    }

    public void changeAddress(User user, String text) {
        if (inArray(text, "✅ Ha", "«❌ Да»")) {
            if (user.getLang().equals("uz")) {
                bot.sendMessage(user.getChatId(), "📍 Iltimos, yuk qaysi manzildan qaysi manzilga kerakligini kiriting:", kyb.back(user.getLang()));
            } else if (user.getLang().equals("ru")) {
                bot.sendMessage(user.getChatId(), "📍 Пожалуйста, укажите, с какого адреса на какой адрес нужен груз:", kyb.back(user.getLang()));
            }
        } else if (inArray(text, "❌ Yo'q", "«❌ Нет»")) {
            start(user);
        } else if (inArray(text, "⬅️ Назад", "⬅️ Orqaga")) {
            menu(user, user.getLang().equals("uz") ? "📍 Manzilni o'zgartirish" : "📍 Изменить адрес");
        } else {
            user.setAddress(text);
            userService.save(user);
            if (user.getLang().equals("uz")) {
                bot.sendMessage(user.getChatId(), "✅ Manzilingiz muvaffaqiyatli o'zgartirildi!", true);
            } else if (user.getLang().equals("ru")) {
                bot.sendMessage(user.getChatId(), "✅ Ваш адрес успешно изменен!", true);
            }
            start(user);
        }
    }

    public void addNewAddress(User user, String text) {
        if (inArray(text, "⬅️ Назад", "⬅️ Orqaga")) start(user);
        else {
            user.setAddress(text);
            userService.save(user);
            if (user.getLang().equals("uz")) {
                bot.sendMessage(user.getChatId(), "✅ Manzilingiz muvaffaqiyatli o'zgartirildi!", true);
            } else if (user.getLang().equals("ru")) {
                bot.sendMessage(user.getChatId(), "✅ Ваш адрес успешно изменен!", true);
            }

            start(user);
        }
    }
}
