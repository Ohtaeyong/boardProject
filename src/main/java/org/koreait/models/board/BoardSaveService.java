package org.koreait.models.board;

import lombok.RequiredArgsConstructor;
import org.koreait.controllers.boards.BoardForm;
import org.koreait.entities.Board;
import org.koreait.entities.BoardData;
import org.koreait.repositories.BoardDataRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardSaveService { // 게시글 저장 서비스

    private final BoardDataRepository boardDataRepository;

    public void save(BoardForm form) {
        Long seq = form.getSeq();
        String mode = Objects.requireNonNullElse(form.getMode(), "add");

        BoardData data = null;
        if (mode.equals("update") && seq != null) {
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new); // 게시글 없을 때 예외처리
        } else {
            data = new BoardData(); // 게시물 없으면 추가
        }

        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setPoster(form.getPoster());

        boardDataRepository.saveAndFlush(data);
        // 컨트롤러로 이동해서 연동
    }
}
