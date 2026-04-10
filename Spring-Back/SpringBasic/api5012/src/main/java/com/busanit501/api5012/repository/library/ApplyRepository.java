package com.busanit501.api5012.repository.library;

import com.busanit501.api5012.domain.library.Apply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ApplyRepository - 시설 예약 신청 레포지토리
 *
 * Apply 엔티티에 대한 DB 접근 인터페이스입니다.
 * 회원별 예약 신청 이력, 관리자의 상태별 예약 관리 기능을 제공합니다.
 */
@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {

    /**
     * findByMemberId - 회원 ID 로 예약 신청 이력 조회 (페이징)
     * 마이페이지에서 내가 신청한 시설 예약 목록을 표시합니다.
     *
     * @param memberId 조회할 회원 ID
     * @param pageable 페이징 정보
     * @return 해당 회원의 예약 신청 이력 (페이징)
     */
    Page<Apply> findByMemberId(Long memberId, Pageable pageable);

    /**
     * findAllByMemberId - 회원 ID 로 해당 회원의 모든 예약 신청을 조회 (List 반환)
     * 관리자 회원 삭제 시 연관 예약 데이터를 정리하기 위해 사용합니다.
     *
     * @param memberId 조회할 회원 ID
     * @return 해당 회원의 전체 예약 신청 리스트
     */
    List<Apply> findAllByMemberId(Long memberId);

    /**
     * findByStatus - 처리 상태별 예약 신청 조회 (관리자용, 페이징)
     * 관리자 화면에서 대기 중(PENDING), 승인(APPROVED), 반려(REJECTED) 건을 분리 조회합니다.
     *
     * @param status   조회할 상태 ("PENDING", "APPROVED", "REJECTED")
     * @param pageable 페이징 정보
     * @return 해당 상태의 예약 신청 목록 (페이징)
     */
    Page<Apply> findByStatus(String status, Pageable pageable);

    /**
     * findByMemberIdAndStatus - 회원의 상태별 예약 조회
     * 마이페이지에서 승인된 예약만, 또는 대기 중인 예약만 표시할 때 사용합니다.
     *
     * @param memberId 회원 ID
     * @param status   조회할 상태
     * @return 조건에 맞는 예약 신청 목록
     */
    List<Apply> findByMemberIdAndStatus(Long memberId, String status);

    /**
     * findByFacilityTypeAndReserveDate - 날짜 + 시설 중복 예약 확인
     * 특정 날짜에 특정 시설이 이미 예약되어 있는지 확인합니다.
     * 중복 예약을 방지하는 유효성 검증에 활용합니다.
     *
     * @param facilityType 시설 유형 ("세미나실", "스터디룸", "강당")
     * @param reserveDate  예약 희망일
     * @return 해당 날짜·시설의 예약 목록
     */
    List<Apply> findByFacilityTypeAndReserveDate(String facilityType, LocalDate reserveDate);

    /**
     * existsByMemberIdAndReserveDateAndStatus - 특정 날짜 중복 신청 확인
     * 같은 회원이 같은 날짜에 이미 예약을 신청했는지 확인합니다.
     *
     * @param memberId    회원 ID
     * @param reserveDate 예약 희망일
     * @param status      대기 상태 ("PENDING")
     * @return 이미 신청이 있으면 true
     */
    boolean existsByMemberIdAndReserveDateAndStatus(Long memberId, LocalDate reserveDate, String status);

    /**
     * findByReserveDateBetween - 기간별 예약 신청 조회
     * 특정 기간 내의 모든 예약 신청을 조회합니다.
     * 관리자 예약 달력 화면에 사용합니다.
     *
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일
     * @return 해당 기간의 예약 목록
     */
    List<Apply> findByReserveDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * findPendingAppliesWithMember - 대기 중 예약 신청 목록 (회원 정보 포함)
     * 관리자 처리 화면에서 신청자 정보와 함께 대기 건을 표시합니다.
     * JOIN FETCH 로 N+1 문제를 방지합니다.
     *
     * @param pageable 페이징 정보
     * @return 회원 정보가 포함된 대기 중 예약 신청 목록
     */
    @Query("SELECT a FROM Apply a " +
           "JOIN FETCH a.member " +
           "WHERE a.status = 'PENDING' " +
           "ORDER BY a.regDate ASC")
    Page<Apply> findPendingAppliesWithMember(Pageable pageable);

    /**
     * countByStatus - 상태별 예약 건수 집계
     * 관리자 대시보드에서 처리 대기 중인 예약 건수를 표시합니다.
     *
     * @param status 집계할 상태
     * @return 해당 상태의 예약 건수
     */
    long countByStatus(String status);

    /**
     * findByFacilityType - 시설 유형별 예약 조회
     * 특정 시설(세미나실, 스터디룸, 강당)의 모든 예약을 조회합니다.
     *
     * @param facilityType 시설 유형
     * @param pageable     페이징 정보
     * @return 해당 시설의 예약 목록 (페이징)
     */
    Page<Apply> findByFacilityType(String facilityType, Pageable pageable);

    /**
     * searchApplies - 신청자명 또는 연락처로 예약 신청 검색 (관리자용)
     *
     * @param keyword  검색 키워드 (신청자명 또는 연락처)
     * @param pageable 페이징 정보
     * @return 검색 결과 목록 (페이징)
     */
    @Query("SELECT a FROM Apply a WHERE " +
           "a.applicantName LIKE %:keyword% OR " +
           "a.phone LIKE %:keyword% " +
           "ORDER BY a.regDate DESC")
    Page<Apply> searchApplies(@Param("keyword") String keyword, Pageable pageable);
}
