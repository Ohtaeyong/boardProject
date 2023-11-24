package org.koreait.restcontrollers.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.exceptions.BadRequestException;
import org.koreait.commons.rest.JSONData;
import org.koreait.controllers.boards.BoardForm;
import org.koreait.entities.BoardData;
import org.koreait.models.board.BoardInfoService;
import org.koreait.models.board.BoardSaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class ApiBoardController { // api형태로 restcontroller

    private final BoardSaveService saveService;
    private final BoardInfoService infoService;

    // 게시글 쓰기
    @PostMapping("/write/{bId}")
    public ResponseEntity write(@PathVariable String bId, @RequestBody @Valid BoardForm form, Errors errors) { // 응답코드 내보낼 때

        if (errors.hasErrors()) { // 에러를 모아서 JSON으로
            String message = errors.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
            throw new BadRequestException(message);
        }

        // 서비스 연동하고 처리
        saveService.save(form);

        return ResponseEntity.status(HttpStatus.CREATED).build(); // 응답코드만 201로 나오게
    }

    // 게시글 조회 (응답코드)
    @GetMapping("/view/{seq}")
    public JSONData<BoardData> view(@PathVariable Long seq) {

        BoardData data = infoService.get(seq);

        return new JSONData<>(data);
        // -> api테스트하러 이동
    }
}
