package org.koreait.models.board;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.Pagination;
import org.koreait.commons.Utils;
import org.koreait.controllers.boards.BoardDataSearch;
import org.koreait.controllers.boards.BoardForm;
import org.koreait.entities.*;
import org.koreait.models.file.FileInfoService;
import org.koreait.repositories.BoardDataRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BoardInfoService { // 목록과 게시글 상세

    private final BoardDataRepository boardDataRepository;
    private final FileInfoService fileInfoService;
    private final EntityManager em;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;
    private final HttpSession session;
    private final PasswordEncoder encoder;

    // 게시글 한개 조회(간단, 이후 추가)
    public BoardData get(Long seq) {

        BoardData data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new); // 게시물 없을시 예외

        addFileInfo(data);

        return data;
    }

    public BoardForm getForm(Long seq) {
        BoardData data = get(seq);
        BoardForm form = new ModelMapper().map(data, BoardForm.class);
        form.setMode("update"); // 모드값 고정
        form.setBId(data.getBoard().getBId()); // id 값을 가져올 수 없다는 오류로 인해 추가

        return form;
    } // controller로 이동 12:12

    public ListData<BoardData> getList(BoardDataSearch search) {
        QBoardData boardData = QBoardData.boardData;
        int page = Utils.getNumber(search.getPage(), 1);
        int limit = Utils.getNumber(search.getLimit(), 20);
        int offset = (page - 1) * limit;

        String bId = search.getBId(); // 게시판 아이디
        String sopt = Objects.requireNonNullElse(search.getSopt(), "subject_content"); // 검색 옵션
        String skey = search.getSkey(); // 검색 키워드 // 12-09 11:29
        String category = search.getCategory(); // 게시판 분류

        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.board.bId.eq(bId));

        // 게시판 분류 검색 처리
        if (StringUtils.hasText(category)) {
            category = category.trim();
            andBuilder.and(boardData.category.eq(category));
        }

        // 키워드 검색 처리
        if (StringUtils.hasText(skey)) {
            skey = skey.trim(); // 공백 제거

            if (sopt.equals("subject")) { // 제목 검색
                andBuilder.and(boardData.subject.contains(skey));

            } else if (sopt.equals("content")) { // 내용 검색
                andBuilder.and(boardData.content.contains(skey));

            } else if (sopt.equals("subject_content")) { // 제목 + 내용 검색
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(boardData.subject.contains(skey))
                        .or(boardData.content.contains(skey)); // 메서드 체인 방식으로

                andBuilder.and(orBuilder);

            } else if (sopt.equals("poster")) { // 작성자 + 아이디 + 회원명
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(boardData.poster.contains(skey))
                        .or(boardData.member.email.contains(skey))
                        .or(boardData.member.userNm.contains(skey));

                andBuilder.and(orBuilder);
            }
        }

        PathBuilder pathBuilder = new PathBuilder(BoardData.class, "boardData");
        List<BoardData> items = new JPAQueryFactory(em)
                .selectFrom(boardData)
                .leftJoin(boardData.board)
                .leftJoin(boardData.member) // 조인을 명시해야 함
                .where(andBuilder)
                .offset(offset)
                .limit(limit)
                .fetchJoin() // * n + 1 -> 세번째 시간 면접대비
                .orderBy(new OrderSpecifier(Order.valueOf("DESC"), pathBuilder.get("createdAt")))
                .fetch();

        // (int page, int total, int ranges, int limit, HttpServletRequest request) -> Pagination 참고
        int total = (int)boardDataRepository.count(andBuilder); // 전체 개수

        Pagination pagination = new Pagination(page, total, 10, limit, request);

        // 파일 정보 추가
        items.stream().forEach(this::addFileInfo);

        ListData<BoardData> data = new ListData<>();
        data.setContent(items);
        data.setPagination(pagination);

        return data;
    }

    // 12-09 추가 이후 위의 getList추가
    private void addFileInfo(BoardData data) {
        String gid = data.getGid();
        List<FileInfo> editorImage = fileInfoService.getListDone(gid, "editor");
        List<FileInfo> attachFiles = fileInfoService.getListDone(gid, "attach"); // 이미지와 파일정보 가져오기

        data.setEditorImages(editorImage);
        data.setAttachFiles(attachFiles);
    }

    // 12-09 14:13 게시글 수정과 삭제 검증
    public boolean isMine(Long seq) {
        if (memberUtil.isAdmin()) { // 관리자는 수정, 삭제 모두 가능
            return true;
        }

        BoardData data = get(seq);
        // 회원 등록 게시물이지만 직접 작성한 게시물인 경우
        if (data.getMember() != null) {
            Member boardMember = data.getMember();
            Member member = memberUtil.getMember();

            return memberUtil.isLogin() && boardMember.getUserNo().longValue() == member.getUserNo().longValue();
        } else { // 비회원 게시글
            // 세션에 chk_게시글번호 항목이 있으면 비번 검증 완료
            String key = "chk_" + seq;
            if (session.getAttribute(key) == null) { // 비회원 비밀번호 검증 X -> 검증 화면으로 이동
                session.setAttribute("guest_seq", seq);

                throw new RequiredPasswordCheckException();
            } else { // 비회원 게시글 검증 성공시
                return true;
            }
        }

    } // boardcontroller로 이동

    // boardController -> guestpassword작성후
    public boolean checkGuestPassword(Long seq, String password) {
        BoardData data = get(seq);
        String guestPw = data.getGuestPw();
        if (!StringUtils.hasText(guestPw)) {
            return false;
        }

        return encoder.matches(password, guestPw); // 비밀번호 검증
    }
}
