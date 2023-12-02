package org.koreait.models.board.config;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.Utils;
import org.koreait.commons.constants.BoardAuthority;
import org.koreait.commons.exceptions.AlertException;
import org.koreait.controllers.admins.BoardConfigForm;
import org.koreait.entities.Board;
import org.koreait.repositories.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardConfigSaveService { // 게시판 설정 저장 (엔티티 레포지토리 생성후)

    private final BoardRepository boardRepository;

    private final Utils utils;

    public void save(BoardConfigForm form) {

        String bId = form.getBId(); // 최초에 추가될 때만 추가
        //String mode = form.getMode(); 통합테스트 오류로인한 수정 11-24
        String mode = Objects.requireNonNullElse(form.getMode(), "add");
        Board board = null; // 수정X일때
        if (mode.equals("edit") && StringUtils.hasText(bId)) {
            board = boardRepository.findById(bId).orElseThrow(BoardNotFoundException::new);
        } else { // 추가
            board = new Board();
            board.setBId(bId); // 최초에 한번만 추가
        }

        board.setBName(form.getBName()); // 필수항목이므로 빠지면 오류발생
        board.setActive(form.isActive());
        board.setAuthority(BoardAuthority.valueOf(form.getAuthority())); // 문자열데이터를 다시 enum상수로 바꿔주는
        board.setCategory(form.getCategory()); // 수정할 데이터를 마저 set해서 넣어줌

        boardRepository.saveAndFlush(board);
        // boardcontroller로 이동 후 save에 연동하면 끝
    }

    /**
     * 게시판 설정 목록 수정
     * @param idxes
     */
    public void update(List<Integer> idxes) {

        if (idxes == null || idxes.isEmpty()) { // idxes가 있을때만
            throw new AlertException("수정할 게시판을 선택하세요.");
        }

        for (int idx : idxes) {
            String bId = utils.getParam("bId_" + idx);
            Board board = boardRepository.findById(bId).orElse(null);
            if (board == null) continue;

            String bName = utils.getParam("bName_" + idx);
            boolean active = Boolean.parseBoolean(utils.getParam("active_" + idx));
            BoardAuthority authority = BoardAuthority.valueOf(utils.getParam("authority_" + idx));

            board.setBName(bName);
            board.setActive(active);
            board.setAuthority(authority);
        }

        boardRepository.flush(); // boardcontroller로 이동
    }
}
