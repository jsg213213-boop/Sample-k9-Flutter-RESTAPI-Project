package com.busanit501.api5012.repository.library;

import com.busanit501.api5012.domain.library.WishBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WishBookRepository - 희망 도서 신청 레포지토리
 *
 * WishBook 엔티티에 대한 DB 접근 인터페이스입니다.
 * 회원별 신청 이력, 처리 상태별 조회, 도서 제목 검색 등의 기능을 제공합니다.
 */
@Repository
public interface WishBookRepository extends JpaRepository<WishBook, Long> {

    /**
     * findByMemberId - 회원 ID 로 희망 도서 신청 이력 조회 (페이징)
     * 마이페이지에서 내가 신청한 희망 도서 목록을 표시합니다.
     *
     * @param memberId 조회할 회원 ID
     * @param pageable 페이징 정보
     * @return 해당 회원의 희망 도서 신청 목록 (페이징)
     */
    Page<WishBook> findByMemberId(Long memberId, Pageable pageable);

    /**
     * findAllByMemberId - 회원 ID 로 해당 회원의 모든 희망도서 신청을 조회 (List 반환)
     * 관리자 회원 삭제 시 연관 희망도서 데이터를 정리하기 위해 사용합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 해당 회원의 전체 희망도서 신청 리스트
     */
    List<WishBook> findAllByMemberId(Long memberId);

    /**
     * findByStatus - 처리 상태별 희망 도서 조회 (관리자용, 페이징)
     * 관리자 화면에서 처리 대기 중(REQUESTED), 승인(APPROVED), 반려(REJECTED) 건을 분리 조회합니다.
     *
     * @param status   조회할 처리 상태 ("REQUESTED", "APPROVED", "REJECTED")
     * @param pageable 페이징 정보
     * @return 해당 상태의 희망 도서 신청 목록 (페이징)
     */
    Page<WishBook> findByStatus(String status, Pageable pageable);

    /**
     * findByMemberIdAndStatus - 회원의 상태별 희망 도서 조회
     * 처리 결과(승인/반려)를 회원에게 알릴 때 필터링하여 사용합니다.
     *
     * @param memberId 회원 ID
     * @param status   조회할 처리 상태
     * @return 조건에 맞는 희망 도서 신청 목록
     */
    List<WishBook> findByMemberIdAndStatus(Long memberId, String status);

    /**
     * findByWishBookTitleContaining - 도서 제목으로 검색
     * 관리자 화면에서 특정 도서 제목의 희망 신청 건을 검색합니다.
     *
     * @param wishBookTitle 검색할 도서 제목 키워드
     * @param pageable      페이징 정보
     * @return 제목에 키워드가 포함된 희망 도서 신청 목록 (페이징)
     */
    Page<WishBook> findByWishBookTitleContaining(String wishBookTitle, Pageable pageable);

    /**
     * existsByMemberIdAndWishBookTitle - 동일 회원의 동일 도서 중복 신청 확인
     * 같은 도서를 이미 신청한 적이 있는지 확인합니다.
     *
     * @param memberId      회원 ID
     * @param wishBookTitle 도서 제목
     * @return 이미 신청이 있으면 true
     */
    boolean existsByMemberIdAndWishBookTitle(Long memberId, String wishBookTitle);

    /**
     * countByStatus - 처리 상태별 희망 도서 신청 건수 집계
     * 관리자 대시보드에서 처리 대기 건수를 표시합니다.
     *
     * @param status 집계할 상태
     * @return 해당 상태의 신청 건수
     */
    long countByStatus(String status);

    /**
     * searchWishBooks - 키워드로 희망 도서 통합 검색 (제목 + 저자 + 출판사)
     * 관리자 화면에서 통합 검색 기능에 사용합니다.
     *
     * @param keyword  검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 결과 목록 (페이징)
     */
    @Query("SELECT w FROM WishBook w WHERE " +
           "w.wishBookTitle LIKE %:keyword% OR " +
           "w.wishAuthor LIKE %:keyword% OR " +
           "w.wishPublisher LIKE %:keyword% " +
           "ORDER BY w.regDate DESC")
    Page<WishBook> searchWishBooks(@Param("keyword") String keyword, Pageable pageable);

    /**
     * findAllWithMember - 전체 희망 도서 신청 목록 (회원 정보 포함, 관리자용)
     * JOIN FETCH 로 N+1 문제를 방지하면서 회원 정보도 함께 조회합니다.
     *
     * @param pageable 페이징 정보
     * @return 회원 정보가 포함된 희망 도서 신청 목록
     */
    @Query("SELECT w FROM WishBook w " +
           "JOIN FETCH w.member " +
           "ORDER BY w.regDate DESC")
    Page<WishBook> findAllWithMember(Pageable pageable);
}
