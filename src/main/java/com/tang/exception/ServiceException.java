package com.tang.exception;

import lombok.Getter;

/**
 * @author tang
 * @date 2022/5/24 11:17
 * @desc
 */
@Getter
public class ServiceException extends RuntimeException{
    private String code;

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }
}
