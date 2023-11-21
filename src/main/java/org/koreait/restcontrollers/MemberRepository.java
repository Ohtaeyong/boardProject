package org.koreait.restcontrollers;

import org.koreait.entities.Member;
import org.koreait.entities.QMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

// CrudRepository -> JpaRepository로 변경후 뒷부분 추가 (조건식을 추가할 때 필요한 인터페이스)
public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {

    Optional<Member> findByEmail(String email);

    default boolean exists(String email) { // 11-21 새로추가 // 조건식 하나일 때는 길게 할 필요없이
        return exists(QMember.member.email.eq(email));
    }
}
