import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../controller/event_controller.dart';

class EventDetailScreen extends StatelessWidget {
  const EventDetailScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 1. 네비게이션 인자(ID) 추출
    final int? eventId = ModalRoute.of(context)?.settings.arguments as int?;

    // 2. 컨트롤러에서 해당 ID의 이벤트 찾기
    final eventCtrl = context.watch<EventController>();
    // 리스트 중 ID가 일치하는 첫 번째 아이템을 찾습니다.
    final event = eventCtrl.eventList.firstWhere(
          (e) => e.id == eventId,
      orElse: () => eventCtrl.eventList.first, // 없을 경우 처리 (실제론 상세조회 API 호출 권장)
    );

    return Scaffold(
      appBar: AppBar(
        title: const Text('행사 상세 정보'),
        centerTitle: true,
      ),
      body: SingleChildScrollView( // 내용이 길어질 수 있으므로 스크롤 적용
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // ── 상단 아이콘 및 ID ──
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Icon(Icons.celebration, color: Colors.orange, size: 40),
                Text('행사 번호: ${event.id}', style: const TextStyle(color: Colors.grey)),
              ],
            ),
            const SizedBox(height: 16),

            // ── 행사 제목 ──
            Text(
              event.title ?? '제목 없음',
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const Divider(height: 40),

            // ── 주요 정보 (날짜, 장소) ──
            _buildIconInfo(Icons.calendar_today, '행사 일시', event.eventDate ?? '날짜 미정'),
            const SizedBox(height: 12),
            _buildIconInfo(Icons.location_on_outlined, '행사 장소', event.location ?? '장소 미정'),

            const SizedBox(height: 30),

            // ── 상세 내용 섹션 ──
            const Text(
              '상세 안내',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.blueGrey),
            ),
            const SizedBox(height: 12),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey[300]!),
              ),
              child: Text(
                event.content ?? '상세 설명이 등록되지 않았습니다.',
                style: const TextStyle(fontSize: 16, height: 1.6),
              ),
            ),

            const SizedBox(height: 40),

            // ── 신청 버튼 ──
            SizedBox(
              width: double.infinity,
              height: 55,
              child: ElevatedButton.icon(
                onPressed: () {
                  // TODO: 행사 신청 로직 연동
                  ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('행사 참가 신청 기능 준비 중입니다.'))
                  );
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blueAccent,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                icon: const Icon(Icons.check_circle_outline, color: Colors.white),
                label: const Text(
                  '행사 참가 신청하기',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.white),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// 아이콘과 함께 정보를 보여주는 헬퍼 위젯
  Widget _buildIconInfo(IconData icon, String label, String value) {
    return Row(
      children: [
        Icon(icon, size: 20, color: Colors.blue),
        const SizedBox(width: 8),
        Text('$label: ', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 15)),
        Expanded(child: Text(value, style: const TextStyle(fontSize: 15))),
      ],
    );
  }
}