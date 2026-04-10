import 'package:busanit501_flutter_workspace_251021/controller/auth/login_controller.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class MyLoginScreen extends StatelessWidget {
  const MyLoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 메인에서 전달 받은 컨트롤러를, 가져와서 이용만 하면 됨.
    // 이 화면을 , 컨트롤러를 지켜보고 있고, 데이터 변경이 감지 되면, 화면을 업데이트 하겠다.
    final loginController = context.watch<LoginController>();

    return Scaffold(
        appBar: AppBar(
          title: const Text('로그인 화면'),
        ),
        body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                const Center(child: FlutterLogo(size:100),),

                const SizedBox(height: 16,),

                TextField(
                    controller: loginController.idController,
                    decoration: const InputDecoration(labelText: '아이디')),

                const SizedBox(height: 16,),

                TextField(
                    controller: loginController.passwordController,
                  //비밀번호 마스킹 처리
                  obscureText: true,
                    decoration: const InputDecoration(labelText: '패스워드')),

                const SizedBox(height: 16,),

                loginController.isLoading ? const CircularProgressIndicator()
                    : Column(
                      children: [
                        SizedBox(
                                          // 목적: 가로 전체 크기 차지
                                          width: double.infinity,
                                          child: ElevatedButton(
                          onPressed: () => loginController.login(context),
                          child: const Text('로그인')),
                                        ),
                        SizedBox(
                          // 목적: 가로 전체 크기 차지
                          width: double.infinity,
                          child: ElevatedButton(
                              onPressed: () =>  Navigator.pushNamed(context, '/signup'),
                              child: const Text('회원가입')),
                        ),
                      ],
                    ),



              ],
            ),
          ),
        ),
      );

  }
}
