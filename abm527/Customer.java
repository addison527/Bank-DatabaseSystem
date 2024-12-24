import java.time.LocalDate;

public class Customer {
    private String name;
    private LocalDate dob;
    private String phone;
    private String address;
    private LocalDate bankAccountOpeningDate;

    public Customer(String name, LocalDate dob, String phone, String address, LocalDate bankAccountOpeningDate) {
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.address = address;
        this.bankAccountOpeningDate = bankAccountOpeningDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBankAccountOpeningDate() {
        return bankAccountOpeningDate;
    }

    public void setBankAccountOpeningDate(LocalDate bankAccountOpeningDate) {
        this.bankAccountOpeningDate = bankAccountOpeningDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", dob=" + dob +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", bankAccountOpeningDate=" + bankAccountOpeningDate +
                '}';
    }
}
