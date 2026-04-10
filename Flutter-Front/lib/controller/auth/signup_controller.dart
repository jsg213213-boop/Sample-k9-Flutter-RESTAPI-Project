import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../../const/api_constants.dart';

/// 회원가입 컨트롤러 (단일 통합 가입 버전)
class SignupController extends ChangeNotifier {
  // 입력 필드 컨트롤러
  final TextEditingController idController = TextEditingController();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController passwordConfirmController = TextEditingController();
  final TextEditingController mnameController = TextEditingController();
  final TextEditingController regionController = TextEditingController();

  // 상태 변수
  bool _isPasswordMatch = false;
  bool get isPasswordMatch => _isPasswordMatch;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  // 지역 선택 드롭다운용 리스트
  final List<String> regions = [
    '서울특별시', '부산광역시', '대구광역시', '인천광역시',
    '광주광역시', '대전광역시', '울산광역시', '세종특별자치시',
    '경기도', '강원특별자치도', '충청북도', '충청남도',
    '전북특별자치도', '전라남도', '경상북도', '경상남도', '제주특별자치도'
  ];

  String? _selectedRegion;
  String? get selectedRegion => _selectedRegion;

  /// 지역 선택 시 호출
  void setRegion(String? value) {
    _selectedRegion = value;
    regionController.text = value ?? ''; // 기존 로직 호환용
    notifyListeners();
  }

  /// 비밀번호 일치 확인
  void validatePassword() {
    _isPasswordMatch = (passwordController.text.isNotEmpty &&
        passwordController.text == passwordConfirmController.text);
    notifyListeners();
  }

  /// 아이디 중복 체크
  Future<void> checkDuplicateId(BuildContext context) async {
    final inputId = idController.text.trim();
    if (inputId.isEmpty) {
      _showDialog(context, '오류', '아이디를 입력하세요.');
      return;
    }
    try {
      final response = await http.get(
        Uri.parse('${ApiConstants.springBaseUrl}/member/check-mid?mid=$inputId'),
      );
      if (!context.mounted) return;

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = jsonDecode(utf8.decode(response.bodyBytes));
        final bool isAvailable = data['available'] ?? false;

        if (isAvailable) {
          _showDialog(context, '사용 가능', '이 아이디는 사용할 수 있습니다.');
        } else {
          _showDialog(context, '중복된 아이디', '이미 사용 중인 아이디입니다.');
        }
      } else {
        _showDialog(context, '오류', '서버 응답 오류: ${response.statusCode}');
      }
    } catch (e) {
      if (!context.mounted) return;
      _showDialog(context, '오류', '네트워크 오류: $e');
    }
  }

  /// [핵심] 단일 통합 회원 가입 처리
  /// API: POST /api/member/signup
  Future<void> signup(BuildContext context) async {
    // 1. 유효성 검사
    if (!_isPasswordMatch) {
      _showDialog(context, '오류', '비밀번호가 일치하지 않습니다.');
      return;
    }

    final mid = idController.text.trim();
    final mpw = passwordController.text.trim();
    final mpwConfirm = passwordConfirmController.text.trim();
    final email = emailController.text.trim();
    final mname = mnameController.text.trim();
    final region = regionController.text.trim();

    if (mid.isEmpty || mpw.isEmpty || email.isEmpty || mname.isEmpty) {
      _showToast(context, '필수 항목(*)을 모두 입력하세요.');
      return;
    }

    _isLoading = true;
    notifyListeners();

    try {
      // 2. 서버로 단일 요청 전송
      final response = await http.post(
        Uri.parse('${ApiConstants.springBaseUrl}/member/signup'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'mid': mid,
          'mpw': mpw,
          'mpwConfirm': mpwConfirm,
          'mname': mname,
          'email': email,
          'region': region.isEmpty ? null : region,
        }),
      );

      if (!context.mounted) return;

      // 3. 응답 결과 처리 (200 OK 또는 201 Created)
      if (response.statusCode == 200 || response.statusCode == 201) {
        _showToast(context, '회원 가입 성공!');
        Future.delayed(const Duration(milliseconds: 800), () {
          if (context.mounted) Navigator.pushReplacementNamed(context, '/login');
        });
      } else {
        // 실패 시 서버 메시지(json의 message 키) 파싱
        final Map<String, dynamic> responseData = jsonDecode(utf8.decode(response.bodyBytes));
        _showToast(context, '가입 실패: ${responseData['message'] ?? '오류가 발생했습니다.'}');
      }
    } catch (e) {
      if (!context.mounted) return;
      _showToast(context, '오류 발생: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // ── 공통 UI 헬퍼 ──────────────────────────────────────────
  void _showDialog(BuildContext context, String title, String message) {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(onPressed: () => Navigator.of(ctx).pop(), child: const Text('확인')),
        ],
      ),
    );
  }

  void _showToast(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), duration: const Duration(seconds: 2)),
    );
  }

  @override
  void dispose() {
    idController.dispose();
    emailController.dispose();
    passwordController.dispose();
    passwordConfirmController.dispose();
    mnameController.dispose();
    regionController.dispose();
    super.dispose();
  }
}