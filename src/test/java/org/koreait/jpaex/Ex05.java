package org.koreait.jpaex;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.entities.BoardData;
import org.koreait.entities.QBoardData;
import org.koreait.repositories.BoardDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
public class Ex05 {

    @Autowired
    private BoardDataRepository repository;

    @BeforeEach // 테스트확인을 위한 데이터 추가
    void init() {
        List<BoardData> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BoardData item = BoardData.builder()
                    .subject("제목" + i)
                    .content("내용" + i)
                    .build();
            items.add(item);
        }

        repository.saveAllAndFlush(items);
    }

    @Test
    void test1() { //
        QBoardData boardData = QBoardData.boardData;
        //BooleanExpression cond1 = boardData.subject.contains("목");

        List<BoardData> items = (List<BoardData>)repository.findAll(boardData.subject.contains(("목")));
        items.stream().forEach(System.out::println);

    }

    @Test
    void test2() {
        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder(); // 아주 중요함
        BooleanBuilder orBuilder = new BooleanBuilder(); // or은 조금 다름
        orBuilder.or(boardData.subject.contains("목"))
                .or(boardData.content.contains("용"));

        andBuilder.and(orBuilder)
                .and(boardData.seq.in(2, 4, 6, 8));
        /*
        builder.and(boardData.subject.contains("목"))
                .and(boardData.content.contains("용"))
                .and(boardData.seq.in(2, 4, 6, 8));
        */
        List<BoardData> items = (List<BoardData>)repository.findAll(andBuilder);
        items.stream().forEach(System.out::println);
    }
}
