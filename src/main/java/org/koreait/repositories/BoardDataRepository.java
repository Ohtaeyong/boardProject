package org.koreait.repositories;

import org.koreait.entities.BoardData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardDataRepository extends JpaRepository<BoardData, Long>, QuerydslPredicateExecutor<BoardData> { // <엔티티명, 기본키>

    @EntityGraph(attributePaths = "member") // 처음부터 fetch해서 가져옴 // 즉시로딩을 바로할수있는 방법 ★
    List<BoardData> findBySubjectContaining(String key);

    List<BoardData> findByCreatedAtBetween(LocalDateTime sdate, LocalDateTime edate, Pageable pageable);

    List<BoardData> findBySubjectContainingOrContentContainingOrderBySeqDesc(String subject, String content);

    @Query("SELECT b FROM BoardData b WHERE b.subject LIKE :key1 OR b.content LIKE :key2 ORDER BY b.seq DESC")
    List<BoardData> getList(@Param("key1") String subject, @Param("key2") String content);

    // JPQL
    @Query("SELECT b FROM BoardData b JOIN FETCH b.member") // FETCH 해당 엔티티를 처음부터 조인해서 가져온다
    List<BoardData> getList2();
}
