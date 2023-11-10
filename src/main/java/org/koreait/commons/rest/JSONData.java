package org.koreait.commons.rest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class JSONData<T> { // 다양한 데이터를 출력하는 용도

    private boolean success = true;
    private HttpStatus status = HttpStatus.OK;

    @NonNull
    private T data; // 성공시 전송할 데이터
    private String message; // 첫째 줄(success)이 false일 때 설정할 메시지
}
