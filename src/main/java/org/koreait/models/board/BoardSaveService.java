package org.koreait.models.board;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.MemberUtil;
import org.koreait.controllers.boards.BoardForm;
import org.koreait.controllers.boards.BoardFormValidator;
import org.koreait.entities.Board;
import org.koreait.entities.BoardData;
import org.koreait.models.board.config.BoardConfigInfoService;
import org.koreait.repositories.BoardDataRepository;
import org.koreait.repositories.FileInfoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardSaveService { // 게시글 저장 서비스

    private final BoardDataRepository boardDataRepository;
    private final BoardConfigInfoService infoService;
    private final MemberUtil memberUtil;
    private final PasswordEncoder encoder; // 시큐리티가 제공하는 기능
    private final FileInfoRepository fileInfoRepository;
    private final BoardFormValidator validator;

    public void save(BoardForm form, Errors errors) {
        validator.validate(form, errors);
        if (errors.hasErrors()) {
            return;
        }

        save(form);
    }

    public void save(BoardForm form) {
        Long seq = form.getSeq();
        String mode = Objects.requireNonNullElse(form.getMode(), "write");

        // 게시판 설정 조회 + 글쓰기 권한 체크
        Board board = infoService.get(form.getBId(), true);

        String gid = form.getGid();

        BoardData data = null;
        if (mode.equals("update") && seq != null) {
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new); // 게시글 없을 때 예외처리
        } else {
            data = new BoardData(); // 게시물 없으면 추가
            data.setBoard(board); // 게시판 bId 최초 글 등록 시 한번만 등록
            data.setGid(gid); // 그룹 ID(GID)는 최초 한번만 등록
            data.setMember(memberUtil.getMember()); // 글 등록 회원 정보도 최초 글 등록시 한번만
        }

        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setPoster(form.getPoster());
        data.setCategory(form.getCategory());

        // 공지사항 여부 - 관리자만 등록, 수정
        if (memberUtil.isAdmin()) {
            data.setNotice(form.isNotice()); // 관리자일때만 공지사항
        }

        // 비회원 비밀번호 처리
        String guestPw = form.getGuestPw();
        if (StringUtils.hasText(guestPw)) {
            data.setGuestPw(encoder.encode(guestPw));
        }

        boardDataRepository.saveAndFlush(data);
        // 컨트롤러로 이동해서 연동

        // 파일 업로드 완료 처리
        fileInfoRepository.processDone(gid);
    }
}
