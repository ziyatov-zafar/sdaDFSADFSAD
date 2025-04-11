package uz.zafar.logisticsapplication.db.domain;

import jakarta.persistence.*;

@Table(name = "load11")
@Entity

public class Load {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToAddressAndFromAddress() {
        return toAddressAndFromAddress;
    }

    public void setToAddressAndFromAddress(String toAddressAndFromAddress) {
        this.toAddressAndFromAddress = toAddressAndFromAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getCarCount() {
        return carCount;
    }

    public void setCarCount(Integer carCount) {
        this.carCount = carCount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAdvance() {
        return advance;
    }

    public void setAdvance(Boolean advance) {
        isAdvance = advance;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdvance(String advance) {
        this.advance = advance;
    }

    @Column(name = "to_address_and_from_address")
    private String toAddressAndFromAddress;

    private String name;
    private String weight;
    private String price;

    @Column(name = "car_count")
    private Integer carCount;

    @Column(name = "full_name")
    private String fullName;

    private String advance;
    private Boolean isAdvance;
    public void setIsAdvance(Boolean isAdvance) {
        this.isAdvance = isAdvance;
    }  public Boolean getIsAdvance() {
        return isAdvance;
    }

    @Column(name = "payment_type")
    private String paymentType;

    private String phone;

/*
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
*/
    private Long userId;

    private Boolean active;
    private String status;
}
