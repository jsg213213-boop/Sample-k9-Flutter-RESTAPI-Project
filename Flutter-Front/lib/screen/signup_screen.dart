import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../controller/auth/signup_controller.dart';

class SignupScreen extends StatelessWidget {
  const SignupScreen({super.key});

  OutlineInputBorder _passwordBorder(bool hasText, bool isMatch) {
    Color color = !hasText ? Colors.grey : (isMatch ? Colors.green : Colors.red);
    return OutlineInputBorder(borderSide: BorderSide(color: color, width: 2.0));
  }

  @override
  Widget build(BuildContext context) {
    final ctrl = context.watch<SignupController>();

    return Scaffold(
      appBar: AppBar(title: const Text('회원 가입')),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: ListView(
            children: [
              // 아이디 + 중복 체크
              Row(
                children: [
                  Expanded(
                    child: TextField(
                      controller: ctrl.idController,
                      decoration: const InputDecoration(
                        labelText: '아이디 *',
                        border: OutlineInputBorder(),
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: () => ctrl.checkDuplicateId(context),
                    child: const Text('중복 체크'),
                  ),
                ],
              ),
              const SizedBox(height: 16),

              // 이름
              TextField(
                controller: ctrl.mnameController,
                decoration: const InputDecoration(
                  labelText: '이름 *',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),

              // 이메일
              TextField(
                controller: ctrl.emailController,
                keyboardType: TextInputType.emailAddress,
                decoration: const InputDecoration(
                  labelText: '이메일 *',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 16),

              // 지역 (선택)
              // 지역 선택 (기존 TextField 제거 후 교체)
              DropdownButtonFormField<String>(
                value: ctrl.selectedRegion, // 컨트롤러의 현재 선택값
                decoration: const InputDecoration(
                  labelText: '지역 선택 *',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.map_outlined), // 아이콘 추가로 가독성 향상
                ),
                hint: const Text('거주 지역을 선택하세요'),
                // 리스트를 드롭다운 아이템으로 변환
                items: ctrl.regions.map((String region) {
                  return DropdownMenuItem<String>(
                    value: region,
                    child: Text(region),
                  );
                }).toList(),
                onChanged: (String? newValue) {
                  ctrl.setRegion(newValue); // 선택 시 컨트롤러 업데이트
                },
                // 선택 안 했을 때의 유효성 검사를 추가하고 싶다면 validator 사용 가능
                validator: (value) => value == null ? '지역을 선택해주세요' : null,
              ),
              const SizedBox(height: 16),

              // 비밀번호
              TextField(
                controller: ctrl.passwordController,
                obscureText: true,
                onChanged: (_) => ctrl.validatePassword(),
                decoration: InputDecoration(
                  labelText: '비밀번호 *',
                  enabledBorder: _passwordBorder(
                    ctrl.passwordController.text.isNotEmpty,
                    ctrl.isPasswordMatch,
                  ),
                  focusedBorder: _passwordBorder(
                    ctrl.passwordController.text.isNotEmpty,
                    ctrl.isPasswordMatch,
                  ),
                ),
              ),
              const SizedBox(height: 16),

              // 비밀번호 확인
              TextField(
                controller: ctrl.passwordConfirmController,
                obscureText: true,
                onChanged: (_) => ctrl.validatePassword(),
                decoration: InputDecoration(
                  labelText: '비밀번호 확인 *',
                  enabledBorder: _passwordBorder(
                    ctrl.passwordConfirmController.text.isNotEmpty,
                    ctrl.isPasswordMatch,
                  ),
                  focusedBorder: _passwordBorder(
                    ctrl.passwordConfirmController.text.isNotEmpty,
                    ctrl.isPasswordMatch,
                  ),
                ),
              ),
              const SizedBox(height: 8),

              if (ctrl.passwordConfirmController.text.isNotEmpty)
                Text(
                  ctrl.isPasswordMatch ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.',
                  style: TextStyle(
                    color: ctrl.isPasswordMatch ? Colors.green : Colors.red,
                    fontSize: 13,
                  ),
                ),
              const SizedBox(height: 24),

              // 가입 버튼
              SizedBox(
                height: 50,
                child: ElevatedButton(
                  onPressed: ctrl.isLoading ? null : () => ctrl.signup(context),
                  child: ctrl.isLoading
                      ? const SizedBox(
                          height: 24,
                          width: 24,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : const Text('회원 가입', style: TextStyle(fontSize: 16)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
