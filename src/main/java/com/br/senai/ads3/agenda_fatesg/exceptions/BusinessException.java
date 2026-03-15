package com.br.senai.ads3.agenda_fatesg.exceptions;

public class BusinessException extends CoreException {
    public BusinessException(String message) { 
        super(message, "Validação de Negócio", "warning"); 
    }
    public BusinessException(String message, Throwable cause) { 
        super(message, "Erro de Negócio", "error"); 
    }
}