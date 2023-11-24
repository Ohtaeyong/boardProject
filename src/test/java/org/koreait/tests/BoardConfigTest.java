package org.koreait.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.entities.Board;
import org.koreait.repositories.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=test")
public class BoardConfigTest {

    @Autowired
    private MockMvc mockMvc; // 기능동작여부를 브라우저가 없어도 가능

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시판 설정 저장 테스트 - 유효성 검사")
    void boardConfigTest() throws Exception { // 검증이 잘 되었는가
        String body = mockMvc.perform(
                post("/admin/board/save")
                        // Content-Type: application/x-www-form-urlencoded
                        .param("mode", "add")
                        .with(csrf()) // 시큐리티일시 추가
                )
                .andDo(print())
                .andExpect(status().isOk()) // 200코드가 맞는가 // 상태코드, 헤더가 맞는지 (Request matcher)
                .andReturn().getResponse().getContentAsString(Charset.forName("UTF-8")); // 응답에 대한.. (body데이터) // getContentAsString() -> 매개변수 있는걸로 쓸 것(한글깨짐)

        assertTrue(body.contains("게시판 아이디"));
        assertTrue(body.contains("게시판 이름"));
    }

    @Test
    @DisplayName("게시판 설정 저장 테스트 - 성공시 302")
    void boardConfigTest2() throws Exception {

        mockMvc.perform(post("/admin/board/save")
                .param("bId", "notice")
                .param("bName", "공지사항")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/admin/board")); // 응답코드가 나오고 실제이동하는 경로가 맞는지(saveService참고)

        // 실제 DB에도 설정 값이 있는지 체크
        Board board = boardRepository.findById("notice").orElse(null);
        assertNotNull(board); // null이 아니어야함

        assertTrue(board.getBName().contains("공지사항")); // 공지사항이 맞는지
    }
}
