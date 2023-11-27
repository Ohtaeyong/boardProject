package org.koreait.models.board.config;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.ListData;
import org.koreait.commons.Pagination;
import org.koreait.commons.Utils;
import org.koreait.controllers.admins.BoardSearch;
import org.koreait.entities.Board;
import org.koreait.repositories.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class BoardConfigInfoService { // 단일 조회, 목록 조회

    private final BoardRepository repository;

    private final HttpServletRequest request;

    public Board get(String bId) {
        Board data = repository.findById(bId).orElseThrow(BoardNotFoundException::new);

        return data;
    }

    // 게시판 목록 (commons의 ListData참고(페이징 등등))
    public ListData<Board> getList(BoardSearch search) { // controllers -> admin -> BoardSearch생성
        BooleanBuilder andBuilder = new BooleanBuilder();

        int page = Utils.getNumber(search.getPage(), 1);
        int limit = Utils.getNumber(search.getLimit(), 20);

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
