import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../model/notice_model.dart';
import '../const/api_constants.dart';

/// 공지사항 게시판의 데이터를 관리하는 컨트롤러
/// API: GET /api/notice?page=0&size=20
class NoticeController extends ChangeNotifier {
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  List<NoticeModel> _noticeList = [];
  List<NoticeModel> get noticeList => _noticeList;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  Future<String?> _getAccessToken() async =>
      await _secureStorage.read(key: "accessToken");


  // NoticeController 내부에 추가
  NoticeModel? _selectedNotice;
  NoticeModel? get selectedNotice => _selectedNotice;

  Future<void> fetchNoticeById(int id) async {
    _isLoading = true;
    _selectedNotice = null; // 이전 데이터 초기화
    notifyListeners();

    try {
      final String? accessToken = await _getAccessToken();
      // 상세 조회 API 엔드포인트 (백엔드 경로 확인 필요: 보통 /api/notice/{id})
      final url = Uri.parse('${ApiConstants.springBaseUrl}/notice/$id');

      final response = await http.get(url, headers: {
        if (accessToken != null) 'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      });

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        _selectedNotice = NoticeModel.fromJson(data); // 여기서 본문(content)이 담긴 모델 생성
      }
    } catch (e) {
      print('공지사항 상세 로드 에러: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 공지사항 목록 조회 (상단 고정 공지 우선 정렬은 서버에서 처리)
  Future<void> fetchNotices({int page = 0, int size = 20}) async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();

    try {
      final String? accessToken = await _getAccessToken();
      final url = Uri.parse(
          '${ApiConstants.springBaseUrl}/notice?page=$page&size=$size');

      final response = await http.get(url, headers: {
        if (accessToken != null) 'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      });

      if (response.statusCode == 200) {
        final Map<String, dynamic> pageData =
            jsonDecode(utf8.decode(response.bodyBytes));
        final List<dynamic> content = pageData['content'] ?? [];
        _noticeList =
            content.map((json) => NoticeModel.fromJson(json)).toList();
      } else {
        _errorMessage = '공지사항 조회 실패 (${response.statusCode})';
        _noticeList = [];
      }
    } catch (e) {
      _errorMessage = '네트워크 오류: $e';
      print('공지사항 불러오기 에러: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}
