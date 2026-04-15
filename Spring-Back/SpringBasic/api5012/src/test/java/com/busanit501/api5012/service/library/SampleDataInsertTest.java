package com.busanit501.api5012.service.library;

import com.busanit501.api5012.domain.library.*;
import com.busanit501.api5012.repository.library.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SampleDataInsertTest - 전체 테이블 샘플 데이터 삽입 테스트
 *
 * 도서관 관리 시스템의 모든 테이블에 현실적인 샘플 데이터를 삽입합니다.
 *
 * [삽입 순서 - FK 의존 관계 순]
 *  1. tbl_lib_member          (회원)
 *  2. tbl_lib_book            (도서)
 *  3. tbl_lib_notice          (공지사항)
 *  4. tbl_lib_rental          (도서 대여 기록)
 *  5. tbl_lib_inquiry + reply  (문의 + 답변)
 *  6. tbl_lib_event           (행사)
 *  7. tbl_lib_event_application(행사 신청)
 *  8. tbl_lib_apply           (시설 예약)
 *  9. tbl_lib_wish_book       (희망 도서)
 *
 * [주의]
 * @Commit 으로 각 테스트 트랜잭션이 실제 DB에 영구 저장됩니다.
 * 개발/테스트 DB 에서만 실행하세요. 운영 DB 에서 실행 금지.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SampleDataInsertTest {

    // ── 리포지토리 주입 ────────────────────────────────────────
    @Autowired private MemberRepository           memberRepository;
    @Autowired private BookRepository             bookRepository;
    @Autowired private NoticeRepository           noticeRepository;
    @Autowired private RentalRepository           rentalRepository;
    @Autowired private InquiryRepository          inquiryRepository;
    @Autowired private ReplyRepository            replyRepository;
    @Autowired private LibraryEventRepository     libraryEventRepository;
    @Autowired private EventApplicationRepository eventApplicationRepository;
    @Autowired private ApplyRepository            applyRepository;
    @Autowired private WishBookRepository         wishBookRepository;

    // ── 테스트 간 ID 공유 (static) ────────────────────────────
    private static final List<Long> memberIds = new ArrayList<>();
    private static final List<Long> bookIds   = new ArrayList<>();
    private static final List<Long> eventIds  = new ArrayList<>();

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ══════════════════════════════════════════════════════════
    // 1. 회원 (tbl_lib_member)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(1)
    @Transactional
    @Commit
    @DisplayName("1. 회원 샘플 데이터 삽입 (ADMIN 1 + USER 5)")
    void insertMembers() {

        String pw = encoder.encode("test1234!");

        List<Member> members = List.of(
            Member.builder()
                .mid("admin")
                .mpw(encoder.encode("admin1234!"))
                .mname("관리자")
                .email("admin@busanlibrary.kr")
                .region("부산광역시 동래구")
                .role(MemberRole.ADMIN)
                .build(),

            Member.builder()
                .mid("user01")
                .mpw(pw)
                .mname("김부산")
                .email("user01@test.com")
                .region("부산광역시 해운대구")
                .role(MemberRole.USER)
                .build(),

            Member.builder()
                .mid("user02")
                .mpw(pw)
                .mname("이서면")
                .email("user02@test.com")
                .region("부산광역시 부산진구")
                .role(MemberRole.USER)
                .build(),

            Member.builder()
                .mid("user03")
                .mpw(pw)
                .mname("박남구")
                .email("user03@test.com")
                .region("부산광역시 남구")
                .role(MemberRole.USER)
                .build(),

            Member.builder()
                .mid("user04")
                .mpw(pw)
                .mname("최기장")
                .email("user04@test.com")
                .region("부산광역시 기장군")
                .role(MemberRole.USER)
                .build(),

            Member.builder()
                .mid("user05")
                .mpw(pw)
                .mname("정사하")
                .email("user05@test.com")
                .region("부산광역시 사하구")
                .role(MemberRole.USER)
                .build()
        );

        memberRepository.saveAll(members).forEach(m -> memberIds.add(m.getId()));

        assertThat(memberIds).hasSize(6);
        System.out.println("✅ 회원 삽입 완료: " + memberIds);
    }

    // ══════════════════════════════════════════════════════════
    // 2. 도서 (tbl_lib_book)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(2)
    @Transactional
    @Commit
    @DisplayName("2. 도서 샘플 데이터 삽입 (10권)")
    void insertBooks() {

        List<Book> books = List.of(
            Book.builder()
                .bookTitle("스프링 부트 핵심 가이드")
                .author("장정우")
                .publisher("위키북스")
                .isbn("9791158393373")
                .description("스프링 부트 3.x 기반의 실무 백엔드 개발 가이드. REST API, JPA, 보안 설정 등을 다룹니다.")
                .status(BookStatus.AVAILABLE)
                .publishDate(LocalDate.of(2022, 10, 5))
                .build(),

            Book.builder()
                .bookTitle("Clean Code")
                .author("Robert C. Martin")
                .publisher("인사이트")
                .isbn("9788966260959")
                .description("클린 코드를 작성하는 방법과 원칙을 소개하는 소프트웨어 공학 명저.")
                .status(BookStatus.RENTED)
                .publishDate(LocalDate.of(2013, 12, 24))
                .build(),

            Book.builder()
                .bookTitle("자바의 정석")
                .author("남궁성")
                .publisher("도우출판")
                .isbn("9788994492032")
                .description("자바 프로그래밍의 기본부터 심화까지 다루는 국내 최고의 자바 입문서.")
                .status(BookStatus.AVAILABLE)
                .publishDate(LocalDate.of(2016, 1, 27))
                .build(),

            Book.builder()
                .bookTitle("HTTP 완벽 가이드")
                .author("David Gourley")
                .publisher("인사이트")
                .isbn("9788966261208")
                .description("웹 개발자라면 반드시 알아야 할 HTTP 프로토콜의 모든 것.")
                .status(BookStatus.AVAILABLE)
                .publishDate(LocalDate.of(2014, 12, 15))
                .build(),

            Book.builder()
                .bookTitle("운영체제 아주 쉬운 세 가지 이야기")
                .author("Remzi H. Arpaci-Dusseau")
                .publisher("홍릉과학출판사")
                .isbn("9791156647829")
                .description("운영체제의 핵심 개념을 이야기 형식으로 쉽게 설명한 교재.")
                .status(BookStatus.RESERVED)
                .publishDate(LocalDate.of(2020, 8, 20))
                .build(),

            Book.builder()
                .bookTitle("도메인 주도 설계")
                .author("Eric Evans")
                .publisher("위키북스")
                .isbn("9788960771949")
                .description("소프트웨어 복잡성을 다루는 DDD(도메인 주도 설계)의 바이블.")
                .status(BookStatus.AVAILABLE)
                .publishDate(LocalDate.of(2011, 7, 25))
                .build(),

            Book.builder()
                .bookTitle("파친코")
                .author("이민진")
                .publisher("인플루엔셜")
                .isbn("9791161340418")
                .description("재일 조선인 가족의 파란만장한 4대에 걸친 이야기.")
                .status(BookStatus.AVAILABLE)
                .publishDate(LocalDate.of(2022, 3, 10))
                .build(),

            Book.builder()
                .bookTitle("채식주의자")
                .author("한강")
                .publisher("창비")
                .isbn("9788936434595")
                .description("한강 작가의 부커상 수상 소설. 식물이 되려는 한 여성의 이야기.")
                .status(BookStatus.RENTED)
                .publishDate(LocalDate.of(2007, 10, 30))
                .build(),

            Book.builder()
                .bookTitle("부산의 역사와 문화")
                .author("부산역사문화대전 편찬위원회")
                .publisher("부산광역시")
                .isbn("9788960771000")
                .description("부산의 역사, 지리, 인물, 문화유산을 집대성한 지역 향토지.")
                .status(BookStatus.AVAILABLE)
                .publishDate(LocalDate.of(2019, 6, 1))
                .build(),

            Book.builder()
                .bookTitle("수학의 아름다움")
                .author("오쥔")
                .publisher("인사이트")
                .isbn("9788966261499")
                .description("구글 엔지니어가 쓴 수학과 프로그래밍의 연결고리 탐구서.")
                .status(BookStatus.LOST)
                .publishDate(LocalDate.of(2019, 3, 20))
                .build()
        );

        bookRepository.saveAll(books).forEach(b -> bookIds.add(b.getId()));

        assertThat(bookIds).hasSize(10);
        System.out.println("✅ 도서 삽입 완료: " + bookIds);
    }

    // ══════════════════════════════════════════════════════════
    // 3. 공지사항 (tbl_lib_notice)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(3)
    @Transactional
    @Commit
    @DisplayName("3. 공지사항 샘플 데이터 삽입 (고정 2 + 일반 4)")
    void insertNotices() {

        List<Notice> notices = List.of(
            Notice.builder()
                .title("[필독] 2025년 도서관 운영 시간 변경 안내")
                .content("안녕하세요. 부산도서관입니다.\n\n2025년 1월부터 운영 시간이 변경됩니다.\n\n" +
                         "• 평일: 09:00 ~ 21:00\n• 주말: 09:00 ~ 18:00\n• 법정 공휴일: 휴관\n\n" +
                         "이용에 참고 부탁드립니다.")
                .writer("도서관 관리자")
                .topFixed(true)
                .build(),

            Notice.builder()
                .title("[공지] 도서 대여 기간 연장 서비스 개시")
                .content("회원님의 편의를 위해 도서 대여 기간 1회 연장 서비스를 개시합니다.\n\n" +
                         "• 연장 가능 기간: 기존 반납일 기준 +7일\n" +
                         "• 연장 방법: 마이페이지 > 대여 현황 > 연장 신청\n" +
                         "• 연체 도서는 연장 불가\n\n많은 이용 바랍니다.")
                .writer("도서관 관리자")
                .topFixed(true)
                .build(),

            Notice.builder()
                .title("2025년 상반기 문화 행사 일정 안내")
                .content("부산도서관 2025년 상반기 문화 행사 일정을 안내드립니다.\n\n" +
                         "• 1월: 신년 독서 토론회\n• 2월: 설 맞이 전통 문화 체험\n" +
                         "• 3월: 봄 독서 캠프\n• 4월: 작가와의 만남\n\n" +
                         "자세한 내용은 행사 메뉴에서 확인하세요.")
                .writer("문화행사팀")
                .topFixed(false)
                .build(),

            Notice.builder()
                .title("도서관 내 음식물 반입 금지 안내")
                .content("쾌적한 독서 환경 조성을 위해 도서관 내 음식물 반입을 금지합니다.\n\n" +
                         "• 물, 음료 포함 일체의 음식물 반입 금지\n" +
                         "• 1층 로비 자판기 이용 가능 (반입 불가)\n\n협조해 주셔서 감사합니다.")
                .writer("도서관 관리자")
                .topFixed(false)
                .build(),

            Notice.builder()
                .title("노후 자료 폐기 및 신규 도서 입수 안내")
                .content("도서관 장서 관리의 일환으로 노후 자료를 정리하고 신규 도서를 입수합니다.\n\n" +
                         "• 폐기 기간: 2025. 01. 13 ~ 01. 17\n" +
                         "• 신규 입수 예정: 약 500종\n\n" +
                         "관련 문의는 자료실(051-000-0000)로 연락 주세요.")
                .writer("자료관리팀")
                .topFixed(false)
                .build(),

            Notice.builder()
                .title("어린이 열람실 리모델링 공사 안내")
                .content("어린이 열람실 환경 개선을 위한 리모델링 공사가 진행됩니다.\n\n" +
                         "• 공사 기간: 2025. 02. 03 ~ 02. 28\n" +
                         "• 공사 기간 중 어린이 열람실 이용 불가\n" +
                         "• 어린이 도서는 1층 안내 데스크에서 대출 가능\n\n불편을 드려 죄송합니다.")
                .writer("시설관리팀")
                .topFixed(false)
                .build()
        );

        List<Notice> saved = noticeRepository.saveAll(notices);
        assertThat(saved).hasSize(6);
        System.out.println("✅ 공지사항 삽입 완료: " + saved.size() + "건");
    }

    // ══════════════════════════════════════════════════════════
    // 4. 도서 대여 (tbl_lib_rental)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(4)
    @Transactional
    @Commit
    @DisplayName("4. 도서 대여 기록 삽입 (대여중 3 + 반납 1 + 연체 1)")
    void insertRentals() {

        assertThat(memberIds).isNotEmpty();
        assertThat(bookIds).isNotEmpty();

        Member m1 = memberRepository.findById(memberIds.get(1)).orElseThrow();
        Member m2 = memberRepository.findById(memberIds.get(2)).orElseThrow();
        Member m3 = memberRepository.findById(memberIds.get(3)).orElseThrow();

        Book b2 = bookRepository.findById(bookIds.get(1)).orElseThrow(); // Clean Code - RENTED
        Book b5 = bookRepository.findById(bookIds.get(4)).orElseThrow(); // 운영체제 - RESERVED
        Book b8 = bookRepository.findById(bookIds.get(7)).orElseThrow(); // 채식주의자 - RENTED
        Book b3 = bookRepository.findById(bookIds.get(2)).orElseThrow(); // 자바의 정석
        Book b4 = bookRepository.findById(bookIds.get(3)).orElseThrow(); // HTTP 완벽 가이드

        LocalDate today = LocalDate.now();

        List<Rental> rentals = List.of(
            // 대여중 - 정상 기한 내
            Rental.builder()
                .member(m1)
                .book(b2)
                .rentalDate(today.minusDays(3))
                .dueDate(today.plusDays(11))
                .status(RentalStatus.RENTING)
                .build(),

            // 대여중 - 정상 기한 내
            Rental.builder()
                .member(m2)
                .book(b8)
                .rentalDate(today.minusDays(7))
                .dueDate(today.plusDays(7))
                .status(RentalStatus.RENTING)
                .build(),

            // 연체 중
            Rental.builder()
                .member(m3)
                .book(b5)
                .rentalDate(today.minusDays(20))
                .dueDate(today.minusDays(6))
                .status(RentalStatus.OVERDUE)
                .build(),

            // 반납 완료
            Rental.builder()
                .member(m1)
                .book(b3)
                .rentalDate(today.minusDays(18))
                .dueDate(today.minusDays(4))
                .returnDate(today.minusDays(5))
                .status(RentalStatus.RETURNED)
                .build(),

            // 반납 완료
            Rental.builder()
                .member(m2)
                .book(b4)
                .rentalDate(today.minusDays(25))
                .dueDate(today.minusDays(11))
                .returnDate(today.minusDays(12))
                .status(RentalStatus.RETURNED)
                .build()
        );

        List<Rental> saved = rentalRepository.saveAll(rentals);
        assertThat(saved).hasSize(5);
        System.out.println("✅ 대여 기록 삽입 완료: " + saved.size() + "건");
    }

    // ══════════════════════════════════════════════════════════
    // 5. 문의 + 답변 (tbl_lib_inquiry, tbl_lib_reply)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(5)
    @Transactional
    @Commit
    @DisplayName("5. 문의사항 + 관리자 답변 삽입 (답변완료 3 + 대기 2)")
    void insertInquiriesAndReplies() {

        Member m1 = memberRepository.findById(memberIds.get(1)).orElseThrow();
        Member m2 = memberRepository.findById(memberIds.get(2)).orElseThrow();
        Member m3 = memberRepository.findById(memberIds.get(3)).orElseThrow();
        Member m4 = memberRepository.findById(memberIds.get(4)).orElseThrow();
        Member m5 = memberRepository.findById(memberIds.get(5)).orElseThrow();

        // ── 답변 완료 문의 1 ──
        Inquiry inq1 = Inquiry.builder()
                .title("도서 대여 기간 연장이 가능한가요?")
                .content("현재 '스프링 부트 핵심 가이드'를 대여 중인데, 반납 기한이 다가오고 있습니다.\n" +
                         "업무 사정으로 반납이 어려울 것 같아서 연장 신청을 하고 싶습니다.\n" +
                         "온라인으로 연장 신청이 가능한지 알고 싶습니다.")
                .writer(m1.getMname())
                .member(m1)
                .secret(false)
                .build();
        Inquiry saved1 = inquiryRepository.save(inq1);

        Reply reply1 = Reply.builder()
                .replyText("안녕하세요, 김부산 회원님!\n\n도서 대여 기간 연장은 마이페이지 > 대여 현황 메뉴에서 " +
                           "온라인으로 1회 연장(+7일)하실 수 있습니다.\n" +
                           "연체 중인 경우에는 연장이 불가하오니 참고 부탁드립니다.\n\n감사합니다.")
                .replier("도서관 관리자")
                .build();
        saved1.addReply(reply1);
        inquiryRepository.save(saved1);

        // ── 답변 완료 문의 2 ──
        Inquiry inq2 = Inquiry.builder()
                .title("희망 도서 신청 처리 기간이 어떻게 되나요?")
                .content("지난주에 희망 도서 신청을 했는데 아직 결과 연락이 없습니다.\n" +
                         "보통 처리 기간이 얼마나 걸리는지 궁금합니다.")
                .writer(m2.getMname())
                .member(m2)
                .secret(false)
                .build();
        Inquiry saved2 = inquiryRepository.save(inq2);

        Reply reply2 = Reply.builder()
                .replyText("안녕하세요, 이서면 회원님!\n\n희망 도서 신청은 신청 후 약 2~4주 이내에 " +
                           "검토 결과를 안내드리고 있습니다.\n" +
                           "구입이 결정된 경우 도서 입수 완료 후 별도 안내드리겠습니다.\n감사합니다.")
                .replier("자료관리팀")
                .build();
        saved2.addReply(reply2);
        inquiryRepository.save(saved2);

        // ── 답변 완료 문의 3 (비밀글) ──
        Inquiry inq3 = Inquiry.builder()
                .title("분실 신고한 도서 변상 방법 문의")
                .content("대여 중이던 도서를 분실하였습니다. 변상 방법과 금액 기준이 궁금합니다.")
                .writer(m3.getMname())
                .member(m3)
                .secret(true)
                .build();
        Inquiry saved3 = inquiryRepository.save(inq3);

        Reply reply3 = Reply.builder()
                .replyText("안녕하세요, 박남구 회원님.\n\n도서 분실 변상은 동일 도서 구입 또는 " +
                           "정가 기준 금액 납부로 처리됩니다.\n" +
                           "자세한 안내는 도서관 자료실(051-000-0000)로 연락 주세요.")
                .replier("도서관 관리자")
                .build();
        saved3.addReply(reply3);
        inquiryRepository.save(saved3);

        // ── 답변 대기 문의 4 ──
        Inquiry inq4 = Inquiry.builder()
                .title("어린이 열람실 공사 중 어린이 도서 대출 방법")
                .content("어린이 열람실 공사 기간 중 어린이 도서를 대출하려면 어떻게 해야 하나요?\n" +
                         "아이와 함께 방문 예정인데 안내 부탁드립니다.")
                .writer(m4.getMname())
                .member(m4)
                .secret(false)
                .build();
        inquiryRepository.save(inq4);

        // ── 답변 대기 문의 5 ──
        Inquiry inq5 = Inquiry.builder()
                .title("도서관 주차장 이용 시간 문의")
                .content("도서관 주차장 이용 가능 시간이 궁금합니다. 저녁 늦게 방문할 때도 이용 가능한가요?")
                .writer(m5.getMname())
                .member(m5)
                .secret(false)
                .build();
        inquiryRepository.save(inq5);

        long total = inquiryRepository.count();
        System.out.println("✅ 문의 + 답변 삽입 완료: 문의 5건 (답변완료 3, 대기 2)");
        assertThat(total).isGreaterThanOrEqualTo(5);
    }

    // ══════════════════════════════════════════════════════════
    // 6. 행사 (tbl_lib_event)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(6)
    @Transactional
    @Commit
    @DisplayName("6. 도서관 행사 샘플 데이터 삽입 (OPEN 3 + CLOSED 1)")
    void insertEvents() {

        LocalDate today = LocalDate.now();

        List<LibraryEvent> events = List.of(
            LibraryEvent.builder()
                .category("문화행사")
                .title("2025 부산 독서의 달 특별 강연 - 한강 작가와의 만남")
                .content("노벨 문학상 수상 작가 한강의 특별 강연입니다.\n\n" +
                         "• 일시: 2025년 4월 23일 오후 2시\n• 장소: 부산도서관 대강당\n" +
                         "• 대상: 성인 일반\n• 참가비: 무료\n\n사전 신청 필수.")
                .eventDate(today.plusDays(14))
                .place("부산도서관 대강당")
                .maxParticipants(100)
                .currentParticipants(42)
                .status("OPEN")
                .build(),

            LibraryEvent.builder()
                .category("강좌")
                .title("독서 토론 클럽 - 4월 모임 (채식주의자)")
                .content("매월 진행되는 독서 토론 클럽입니다.\n\n" +
                         "• 도서: 채식주의자 (한강)\n• 일시: 2025년 4월 12일 오후 3시\n" +
                         "• 장소: 2층 세미나실\n• 인원: 선착순 15명\n• 참가비: 무료")
                .eventDate(today.plusDays(7))
                .place("2층 세미나실")
                .maxParticipants(15)
                .currentParticipants(9)
                .status("OPEN")
                .build(),

            LibraryEvent.builder()
                .category("주말극장")
                .title("주말 가족 영화 상영 - 어린이날 특집")
                .content("어린이날을 맞아 가족이 함께 즐길 수 있는 영화를 상영합니다.\n\n" +
                         "• 상영작: 인사이드 아웃 2\n• 일시: 2025년 5월 5일 오후 2시\n" +
                         "• 장소: 부산도서관 시청각실\n• 대상: 가족 단위 (전 연령)")
                .eventDate(today.plusDays(30))
                .place("시청각실")
                .maxParticipants(60)
                .currentParticipants(18)
                .status("OPEN")
                .build(),

            LibraryEvent.builder()
                .category("강좌")
                .title("시니어 스마트폰 활용 강좌 (마감)")
                .content("어르신을 위한 스마트폰 기초 활용법 강좌입니다.\n\n" +
                         "• 일시: 2025년 3월 20일\n• 장소: 1층 컴퓨터 교육실\n" +
                         "• 대상: 60세 이상 어르신\n• 정원: 20명 (마감)")
                .eventDate(today.minusDays(5))
                .place("1층 컴퓨터 교육실")
                .maxParticipants(20)
                .currentParticipants(20)
                .status("CLOSED")
                .build()
        );

        libraryEventRepository.saveAll(events).forEach(e -> eventIds.add(e.getId()));

        assertThat(eventIds).hasSize(4);
        System.out.println("✅ 행사 삽입 완료: " + eventIds);
    }

    // ══════════════════════════════════════════════════════════
    // 7. 행사 신청 (tbl_lib_event_application)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(7)
    @Transactional
    @Commit
    @DisplayName("7. 행사 신청 샘플 데이터 삽입 (APPLIED 4 + CANCELLED 1)")
    void insertEventApplications() {

        assertThat(memberIds).isNotEmpty();
        assertThat(eventIds).isNotEmpty();

        Member m1 = memberRepository.findById(memberIds.get(1)).orElseThrow();
        Member m2 = memberRepository.findById(memberIds.get(2)).orElseThrow();
        Member m3 = memberRepository.findById(memberIds.get(3)).orElseThrow();
        Member m4 = memberRepository.findById(memberIds.get(4)).orElseThrow();
        Member m5 = memberRepository.findById(memberIds.get(5)).orElseThrow();

        LibraryEvent ev1 = libraryEventRepository.findById(eventIds.get(0)).orElseThrow();
        LibraryEvent ev2 = libraryEventRepository.findById(eventIds.get(1)).orElseThrow();
        LibraryEvent ev4 = libraryEventRepository.findById(eventIds.get(3)).orElseThrow();

        List<EventApplication> applications = List.of(
            EventApplication.builder().event(ev1).member(m1).status("APPLIED").build(),
            EventApplication.builder().event(ev1).member(m2).status("APPLIED").build(),
            EventApplication.builder().event(ev2).member(m3).status("APPLIED").build(),
            EventApplication.builder().event(ev4).member(m4).status("APPLIED").build(),
            EventApplication.builder().event(ev1).member(m5).status("CANCELLED").build()
        );

        List<EventApplication> saved = eventApplicationRepository.saveAll(applications);
        assertThat(saved).hasSize(5);
        System.out.println("✅ 행사 신청 삽입 완료: " + saved.size() + "건");
    }

    // ══════════════════════════════════════════════════════════
    // 8. 시설 예약 (tbl_lib_apply)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(8)
    @Transactional
    @Commit
    @DisplayName("8. 시설 예약 신청 삽입 (PENDING 2 + APPROVED 1 + REJECTED 1)")
    void insertApplies() {

        Member m1 = memberRepository.findById(memberIds.get(1)).orElseThrow();
        Member m2 = memberRepository.findById(memberIds.get(2)).orElseThrow();
        Member m3 = memberRepository.findById(memberIds.get(3)).orElseThrow();
        Member m4 = memberRepository.findById(memberIds.get(4)).orElseThrow();

        LocalDate today = LocalDate.now();

        List<Apply> applies = List.of(
            // 승인 완료
            Apply.builder()
                .member(m1)
                .applicantName(m1.getMname())
                .facilityType("세미나실")
                .phone("010-1234-5678")
                .participants(8)
                .reserveDate(today.plusDays(5))
                .status("APPROVED")
                .build(),

            // 반려
            Apply.builder()
                .member(m2)
                .applicantName(m2.getMname())
                .facilityType("강당")
                .phone("010-2345-6789")
                .participants(150)
                .reserveDate(today.plusDays(3))
                .status("REJECTED")
                .build(),

            // 대기 중
            Apply.builder()
                .member(m3)
                .applicantName(m3.getMname())
                .facilityType("스터디룸")
                .phone("010-3456-7890")
                .participants(4)
                .reserveDate(today.plusDays(10))
                .status("PENDING")
                .build(),

            // 대기 중
            Apply.builder()
                .member(m4)
                .applicantName(m4.getMname())
                .facilityType("세미나실")
                .phone("010-4567-8901")
                .participants(12)
                .reserveDate(today.plusDays(15))
                .status("PENDING")
                .build()
        );

        List<Apply> saved = applyRepository.saveAll(applies);
        assertThat(saved).hasSize(4);
        System.out.println("✅ 시설 예약 삽입 완료: " + saved.size() + "건");
    }

    // ══════════════════════════════════════════════════════════
    // 9. 희망 도서 (tbl_lib_wish_book)
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(9)
    @Transactional
    @Commit
    @DisplayName("9. 희망 도서 신청 삽입 (REQUESTED 2 + APPROVED 1 + REJECTED 1)")
    void insertWishBooks() {

        Member m1 = memberRepository.findById(memberIds.get(1)).orElseThrow();
        Member m2 = memberRepository.findById(memberIds.get(2)).orElseThrow();
        Member m3 = memberRepository.findById(memberIds.get(3)).orElseThrow();
        Member m4 = memberRepository.findById(memberIds.get(4)).orElseThrow();

        List<WishBook> wishBooks = List.of(
            // 처리 대기
            WishBook.builder()
                .member(m1)
                .wishBookTitle("Effective Java 3rd Edition")
                .wishAuthor("Joshua Bloch")
                .wishPublisher("인사이트")
                .reason("자바 개발자라면 꼭 읽어야 할 필독서입니다. 현재 도서관에 소장되어 있지 않아 신청합니다.")
                .status("REQUESTED")
                .build(),

            // 처리 대기
            WishBook.builder()
                .member(m2)
                .wishBookTitle("데이터 중심 애플리케이션 설계")
                .wishAuthor("마틴 클레퍼만")
                .wishPublisher("위키북스")
                .reason("백엔드 개발자들의 필수 교재로 최근 수요가 높습니다.")
                .status("REQUESTED")
                .build(),

            // 구입 승인
            WishBook.builder()
                .member(m3)
                .wishBookTitle("부산 100년의 기억")
                .wishAuthor("부산일보사")
                .wishPublisher("부산일보사")
                .reason("지역 향토 자료로서 보존 가치가 높은 도서입니다.")
                .status("APPROVED")
                .build(),

            // 반려 (절판)
            WishBook.builder()
                .member(m4)
                .wishBookTitle("실전 스프링 부트와 JPA 활용 1")
                .wishAuthor("김영한")
                .wishPublisher("인프런")
                .reason("스프링 JPA 학습을 위한 핵심 교재입니다.")
                .status("REJECTED")
                .build()
        );

        List<WishBook> saved = wishBookRepository.saveAll(wishBooks);
        assertThat(saved).hasSize(4);
        System.out.println("✅ 희망 도서 삽입 완료: " + saved.size() + "건");
    }

    // ══════════════════════════════════════════════════════════
    // 10. 전체 삽입 결과 검증
    // ══════════════════════════════════════════════════════════

    @Test
    @Order(10)
    @DisplayName("10. 전체 삽입 결과 카운트 검증")
    void verifyCounts() {

        System.out.println("\n══════════════════════════════════════");
        System.out.println(" 샘플 데이터 삽입 결과");
        System.out.println("══════════════════════════════════════");
        System.out.printf(" tbl_lib_member            : %3d건%n", memberRepository.count());
        System.out.printf(" tbl_lib_book              : %3d건%n", bookRepository.count());
        System.out.printf(" tbl_lib_notice            : %3d건%n", noticeRepository.count());
        System.out.printf(" tbl_lib_rental            : %3d건%n", rentalRepository.count());
        System.out.printf(" tbl_lib_inquiry           : %3d건%n", inquiryRepository.count());
        System.out.printf(" tbl_lib_reply             : %3d건%n", replyRepository.count());
        System.out.printf(" tbl_lib_event             : %3d건%n", libraryEventRepository.count());
        System.out.printf(" tbl_lib_event_application : %3d건%n", eventApplicationRepository.count());
        System.out.printf(" tbl_lib_apply             : %3d건%n", applyRepository.count());
        System.out.printf(" tbl_lib_wish_book         : %3d건%n", wishBookRepository.count());
        System.out.println("══════════════════════════════════════\n");

        assertThat(memberRepository.count()).isGreaterThanOrEqualTo(6);
        assertThat(bookRepository.count()).isGreaterThanOrEqualTo(10);
        assertThat(noticeRepository.count()).isGreaterThanOrEqualTo(6);
        assertThat(rentalRepository.count()).isGreaterThanOrEqualTo(5);
        assertThat(inquiryRepository.count()).isGreaterThanOrEqualTo(5);
        assertThat(replyRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(libraryEventRepository.count()).isGreaterThanOrEqualTo(4);
        assertThat(eventApplicationRepository.count()).isGreaterThanOrEqualTo(5);
        assertThat(applyRepository.count()).isGreaterThanOrEqualTo(4);
        assertThat(wishBookRepository.count()).isGreaterThanOrEqualTo(4);
    }
}
