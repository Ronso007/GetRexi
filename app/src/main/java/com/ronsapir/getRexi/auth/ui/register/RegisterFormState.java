package com.ronsapir.getRexi.auth.ui.register;

import androidx.annotation.Nullable;

import com.ronsapir.getRexi.auth.ui.login.LoginFormState;

public class RegisterFormState extends LoginFormState {
    @Nullable
    private Integer nameError;

    private Integer phoneError;

    public RegisterFormState(@Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer NameError, @Nullable Integer phoneError) {
        super(emailError, passwordError);
        this.nameError = NameError;
        this.phoneError = phoneError;
    }

    public RegisterFormState(boolean isDataValid) {
        super(isDataValid);
        this.nameError = null;
    }

    @Nullable
    Integer getUsernameError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getNameError() {
        return nameError;
    }

    @Nullable
    public Integer getPhoneError() {
        return phoneError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
