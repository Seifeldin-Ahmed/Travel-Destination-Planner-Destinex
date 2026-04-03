package com.Destinex.app.dto.input;

import com.Destinex.app.validation.PasswordMatches;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@PasswordMatches
public class SignupRequest {

    @NotNull(message = "is required")
    @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @NotNull(message = "is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z]).{6,}$",
            message = "Password must be at least 6 characters, with 1 uppercase and 1 lowercase"
    )
    private String password;

    @NotNull(message = "is required")
    private String confirmPassword;

    @NotNull(message = "is required")
    @Size(min = 2, message = "is required")
    private String firstName;

    @NotNull(message = "is required")
    @Size(min = 2, message = "is required")
    private String lastName;

    private String address;

    @Pattern(regexp = "\\d{11}", message = "Phone number must have exactly 11 digits")
    private String phoneNumber;


    public SignupRequest() {
    }

    public SignupRequest(String email, String password, String confirmPassword, String firstName, String lastName, String address, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
