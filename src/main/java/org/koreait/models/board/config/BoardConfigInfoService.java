package org.koreait.models.board.config;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.commons.MemberUtil;
import org.koreait.commons.Pagination;
import org.koreait.commons.Utils;
import org.koreait.commons.constants.BoardAuthority;
import org.koreait.commons.exceptions.AuthorizationException;
import org.koreait.controllers.admins.BoardConfigForm;
import org.koreait.controllers.admins.BoardSearch;
import org.koreait.entities.Board;
import org.koreait.entities.QBoard;
import org.koreait.repositories.BoardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class BoardConfigInfoService { // 단일 조회, 목록 조회

    private final BoardRepository repository;

    private final HttpServletRequest request;

    private final MemberUtil memberUtil;

    public Board get(String bId) {
        Board data = repository.findById(bId).orElseThrow(BoardNotFoundException::new);

        return data;
    }

    // 12-09 추가
    public Board get(String bId, boolean checkAuthority) {
        Board data = get(bId);
        if (!checkAuthority) {
            return data;
        }

        // 글쓰기 권한 체크
        BoardAuthority authority = data.getAuthority();
        if (authority != BoardAuthority.ALL) {
            if (!memberUtil.isLogin()) {
                throw new AuthorizationException();
            }

            if (authority == BoardAuthority.ADMIN) {
                throw new AuthorizationException();
            }
        }

        return data;
    }

    // 검색기능 추가 후 오른쪽 사이드버튼 오류 해결
    public BoardConfigForm getForm(String bId) {
        Board board = get(bId);

        BoardConfigForm form = new ModelMapper().map(board, BoardConfigForm.class);
        form.setAuthority(board.getAuthority().name());
        form.setMode("edit");

        return form;
    }

    // 게시판 목록 (commons의 ListData참고(페이징 등등))
    public ListData<Board> getList(BoardSearch search) { // controllers -> admin -> BoardSearch생성
        BooleanBuilder andBuilder = new BooleanBuilder();

        int page = Utils.getNumber(search.getPage(), 1);
        int limit = Utils.getNumber(search.getLimit(), 20);



        /* 검색 처리 S */ // 12-02
        QBoard board = QBoard.board;
        // 통합 검색
        String sopt = Objects.requireNonNullElse(search.getSopt(), "ALL");
        String skey = search.getSkey();


        if (StringUtils.hasText(skey)) { // skey가 있으면
            skey = skey.trim(); // 공백제거

            if (sopt.equals("bId")) { // 게시판 아이디
                andBuilder.and(board.bId.contains(skey));

            } else if (sopt.equals("bName")) { // 게시판 이름
                andBuilder.and(board.bName.contains(skey));

            } else { // 통합검색
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(board.bId.contains(skey))
                        .or(board.bName.contains(skey)); // 아이디도 필요하고 게시판 이름도 필요

                andBuilder.and(orBuilder);
            }
        }

        // 사용 여부
        List<Boolean> active = search.getActive();
        if (active != null && !active.isEmpty()) { // null이 아니고 비어있지 않을 때
            andBuilder.and(board.active.in(active));
        }

        // 글쓰기 권한
        List<BoardAuthority> authorities = search.getAuthority() == null ? null : search.getAuthority().stream().map(BoardAuthority::valueOf).toList(); // (BoardAuthority::valueOf) -> 실제 문자열 데이터를 enum상수로 바꿔주고
        if (authorities != null && !authorities.isEmpty()) {
            andBuilder.and(board.authority.in(authorities));
        }
        /* 검색 처리 E */

        // Sort.Order.desc("엔티티 속성명"), Sort.Order.asc("엔티티 속성명")
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt"))); // PageRequset 0부터 시작, sort 정렬

        Page<Board> data = repository.findAll(andBuilder, pageable);

        // Pagination(int page, int total, int ranges, int limit, HttpServletRequest request) 참고
        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request); // 반환값이 long값이므로 int로 형변환

        ListData<Board> listData = new ListData<>();
        listData.setContent(data.getContent());
        listData.setPagination(pagination);


        return listData;
    }
}
