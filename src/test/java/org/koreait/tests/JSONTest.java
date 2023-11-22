package org.koreait.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.entities.Member;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class JSONTest { // configs 디렉토리의 service들 참조 , Class class, typereference

    private ObjectMapper om;

    @BeforeEach
    void init() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    @Test
    void test1() throws JsonProcessingException {
        Member member = Member.builder()
                .email("user01@test.org")
                .password("12345678")
                .userNm("사용자01")
                .build();
        member.setCreatedAt(LocalDateTime.now());

        String json = om.writeValueAsString(member);
        System.out.println(json);

        // 문자열 객체에서 자바 객체로
        Member member2 = om.readValue(json, Member.class); // 단일객체일때 가능하다
        System.out.println(member2);
    }

    @Test
    void test2() throws JsonProcessingException {
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Member member = Member.builder()
                    .email("user01@test.org")
                    .password("12345678")
                    .userNm("사용자01")
                    .build();

            members.add(member);
        }

        String json = om.writeValueAsString(members);
        System.out.println(json);

        List<Member> members2 = om.readValue(json, new TypeReference<List<Member>>() {}); // collection형태는 reference사용

        members2.stream().forEach(System.out::println);
    }
}
