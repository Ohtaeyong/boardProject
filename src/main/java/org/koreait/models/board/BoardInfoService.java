package org.koreait.models.board;

import lombok.RequiredArgsConstructor;
import org.koreait.entities.BoardData;
import org.koreait.repositories.BoardDataRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardInfoService { // 목록과 게시글 상세

    private final BoardDataRepository boardDataRepository;

    // 게시글 한개 조회(간단, 이후 추가)
    public BoardData get(Long seq) {

        BoardData data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new); // 게시물 없을시 예외

        return data;
    }
}
