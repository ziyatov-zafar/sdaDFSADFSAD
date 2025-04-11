package uz.zafar.logisticsapplication.db.domain;


import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String nickname;
    private String username;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getLang() {
        return lang;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;


    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getHelperPhone() {
        return helperPhone;
    }

    public void setHelperPhone(String helperPhone) {
        this.helperPhone = helperPhone;
    }

    public String getFindUsername() {
        return findUsername;
    }

    public void setFindUsername(String findUsername) {
        this.findUsername = findUsername;
    }

    public String getFindNickname() {
        return findNickname;
    }

    public void setFindNickname(String findNickname) {
        this.findNickname = findNickname;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getHelperRole() {
        return helperRole;
    }

    public void setHelperRole(String helperRole) {
        this.helperRole = helperRole;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    @Column(unique = true)
    private Long chatId;
    private Integer page ;
    private String phone;
    private String role;
    private String eventCode;
    private Long userId;
    private Long channelId;
    private String lang;
    private String helperPhone ;
    private String findUsername;
    private String findNickname;
    private Integer messageId;
    private String helperRole;
    private Long serviceId;
    private Long countryId;
/*
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Load> loads;
*/
    private Long loadId;



}
