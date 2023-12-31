package org.koreait.commons.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class CommonException extends RuntimeException{
    private HttpStatus status;

    public CommonException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }

    public CommonException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    //BoardNotFoundException 추가후


//    public HttpStatus getStatus() {
//        return status;
//    } // setter getter 추가했으므로 제거
}