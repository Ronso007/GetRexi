package com.ronsapir.getRexi.auth.ui.register;

import androidx.annotation.Nullable;

import com.ronsapir.getRexi.auth.ui.login.LoginFormState;

public class RegisterFormState extends LoginFormState {
    @Nullable
    private Integer nameError;

    RegisterFormState(@Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer NameError) {
        super(emailError, passwordError);
        this.nameError = NameError;
    }

    RegisterFormState(boolean isDataValid) {
        super(isDataValid);
        this.nameError = null;
    }

    @Nullable
    Integer getUsernameError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getNameError() {
        return nameError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
