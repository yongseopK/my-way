package com.healthmap.myway.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidateEmailException extends RuntimeException {

    public ValidateEmailException(String message) { super(message);}
}
