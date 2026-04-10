import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../controller/notice_controller.dart';
import '../../const/api_constants.dart';

class NoticeDetailScreen extends StatefulWidget {
  const NoticeDetailScreen({super.key});

  @override
  State<NoticeDetailScreen> createState() => _NoticeDetailScreenState();
}

class _NoticeDetailScreenState extends State<NoticeDetailScreen> {
  int? _noticeId;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // 1. 전달받은 ID 추출
    final args = ModalRoute.of(context)?.settings.arguments;
    if (args is int && _noticeId != args) {
      _noticeId = args;
      // 2. 서버에 상세 데이터 요청 (본문 내용을 가져오기 위함)
      Future.microtask(() {
        if (mounted) context.read<NoticeController>().fetchNoticeById(_noticeId!);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final noticeCtrl = context.watch<NoticeController>();
    final notice = noticeCtrl.selectedNotice; // 새로 받아온 상세 데이터 사용

    return Scaffold(
      appBar: AppBar(title: const Text('공지사항 상세')),
      // 로딩 중이거나 데이터가 없을 때 처리
      body: noticeCtrl.isLoading
          ? const Center(child: CircularProgressIndicator())
          : notice == null
          ? const Center(child: Text('내용을 불러올 수 없습니다.'))
          : SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // --- 상단 태그 및 제목 (기존과 동일) ---
            Row(
              children: [
                if (notice.topFixed == true)
                  _buildBadge('필독', Colors.red),
                Text('공지 No. ${notice.id}', style: const TextStyle(color: Colors.grey, fontSize: 13)),
              ],
            ),
            const SizedBox(height: 12),
            Text(
              notice.title ?? '제목 없음',
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold, height: 1.4),
            ),
            const SizedBox(height: 16),

            // 작성자 및 날짜
            _buildInfoRow(notice),
            const Divider(height: 40, thickness: 1),

            // --- 이미지 목록 ---
            if (notice.images != null && notice.images!.isNotEmpty) ...[
              const Text('첨부 이미지', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey)),
              const SizedBox(height: 10),
              ...notice.images!.map((img) => _buildImage(img.fileName!)).toList(),
              const SizedBox(height: 20),
            ],

            // --- 본문 내용 (이제 fetchNoticeById를 통해 데이터가 들어옵니다!) ---
            const Text('공지 내용', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.grey, fontSize: 14)),
            const SizedBox(height: 12),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey[50],
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.grey[200]!),
              ),
              child: SelectableText(
                notice.content ?? '내용이 없습니다.',
                style: const TextStyle(fontSize: 16, height: 1.8, color: Colors.black87),
              ),
            ),
            const SizedBox(height: 100),
          ],
        ),
      ),
    );
  }

  // ── 위젯 헬퍼 함수들 ──────────────────────────────────────────

  Widget _buildBadge(String text, Color color) {
    return Container(
      margin: const EdgeInsets.only(right: 8),
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(4)),
      child: Text(text, style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold)),
    );
  }

  Widget _buildInfoRow(notice) {
    return Row(
      children: [
        const Icon(Icons.person_pin, size: 16, color: Colors.blueGrey),
        const SizedBox(width: 4),
        Text(notice.writer ?? '관리자', style: const TextStyle(color: Colors.blueGrey)),
        const SizedBox(width: 16),
        const Icon(Icons.calendar_today, size: 14, color: Colors.grey),
        const SizedBox(width: 4),
        Text(notice.regDate ?? '-', style: const TextStyle(color: Colors.grey)),
      ],
    );
  }

  Widget _buildImage(String fileName) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 15),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(8),
        child: Image.network(
          '${ApiConstants.springBaseUrl}/view/$fileName',
          fit: BoxFit.cover,
          width: double.infinity,
          errorBuilder: (ctx, _, __) => Container(
            height: 100, color: Colors.grey[200], child: const Icon(Icons.broken_image, color: Colors.grey),
          ),
        ),
      ),
    );
  }
}