package com.example.demo.controller;

import com.example.demo.DTO.MemberDTO;
import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;
import com.example.demo.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // REST API 컨트롤러로 등록
@RequiredArgsConstructor //final 필드의 생성자를 자동 생성
@RequestMapping("/api") // 공통 URL 경로 설정
public class MemberController {

    //히히

    // 회원 관련 비즈니스 로직을 처리하는 서비스
    private final MemberService memberService;

    // JWT 토큰 검증 및 처리 유틸 클래스--?
    private final JwtUtil jwtUtil;

    //회원가입 API | POST /api/members
    @PostMapping("/members")
    public MemberDTO.Result<Long> saveMember(@RequestBody MemberDTO.Request.Create request){

        Member member = new Member();//DTO로 받은 데이터를 Member 객체로 변환
        member.setUserId(request.getUserId());// 회원 아이디 저장
        member.setPassword(request.getPassword());// 회원 비밀번호 저장
        member.setUsername(request.getUsername());// 회원 이름 저장

        Long id = memberService.signUp(member);//회원 저장 후 생성된 회원 id 반환

        return new MemberDTO.Result<>(id);//결과 데이터를 Result 객체로 감싸서 반환
    }

    //로그인 API | POST /api/Login
    @PostMapping("/login")//생성 /api/login
    public MemberDTO.Result<String> login(@RequestBody MemberDTO.Request.Login request){
        String token = memberService.login(request.getUserId(),request.getPassword());//아이디와 비밀번호를 서비스에 전달하여 JWT 토큰 생성
        return new MemberDTO.Result<>(token);//생성된 토큰 반환
    }//login

    // 전체 회원 조회 API | GET /api/members
    @GetMapping("/members")
    public MemberDTO.Result<List<MemberDTO.Response.Member>> findAllMembers(){
        List<Member> findMembers = memberService.findAll();//전체 회원 조회

        //Member 엔티티를 응답 DTO로 변환
        List<MemberDTO.Response.Member> collect = findMembers.stream()
                //각 회원 객체를 DTO로 변경
                .map(m-> new MemberDTO.Response.Member(m.getId(),m.getUserId(),m.getUsername()))
                .collect(Collectors.toList());//리스트로 변환
        return new MemberDTO.Result<>(collect);//응답 반환
    }

    @PutMapping("/members") //회원 수정 API | PUT /api/members
    public MemberDTO.Result<?> updateMember(
            @RequestBody MemberDTO.Request.Update request, //수정할 데이터 요청
            @RequestHeader("Authorization") String token) { // 요청 헤더의 JWT 토큰
        if (!jwtUtil.validateJwt(token)){ //JWT 토큰 유효성 검사
            return new MemberDTO.Result<>("유효한 토큰이 아닙니다."); //토큰이 유효하지 않을 경우 메시지 반환
        }

        Long id = memberService.tokenToMember(token).getId(); //토큰에서 회원 정보 추출 후 회원 id 조회
        memberService.update(id, request.getUsername(), request.getPassword()); // 회원 정보 수정

        Member findmember = memberService.findById(id); //수정된 회원 조회
        return new MemberDTO.Result<>( //수정된 회원 정보 반환
                new MemberDTO.Response.Member(findmember.getId(), findmember.getUserId(),findmember.getUsername()));
    }

    @DeleteMapping("/members") //회원 삭제 API | DELETE /api/members
    public MemberDTO.Result<String> deleteMember(@RequestHeader("Authorization") String token){ //@RequestHeader~~ 요청 헤더의 JWT 토큰
        if(!jwtUtil.validateJwt(token)){ //JWT 토큰 검증
            return new MemberDTO.Result<>("유효한 토큰이 아닙니다.");//유효하지 않은 토큰이면 메시지 반환
        }

        Long id = memberService.tokenToMember(token).getId(); //토큰에서 회원 id 추출
        memberService.delete(id); //회원 삭제

        return new MemberDTO.Result<>("회원삭제 완료"); //삭제 완료 메시지 반환
    }


}
/*
MemberController 역할
- 클라이언트의 HTTP 요청을 받는 계층이다
- 요청 데이터를 DTO로 전달받아 Service 계층에 전달한다
- Service의 처리 결과를 응답 DTO 형태로 반환한다

요청 흐름

회원가입
클라이언트 요청
-> Controller
-> DTO 생성
-> Service 호출
-> Repository 저장
-> 결과 반환

로그인
클라이언트 요청
-> Controller
-> Service 로그인 처리
-> JWT 토큰 생성
-> 토큰 반환

회원 수정/식제
클라이언트 요청
-> JWT 토큰 검증
-> 토큰에서 회원 정보 추출
-> Service에서 수정/삭제 처리
-> 결과 반환

왜 Controller가 필요한가?
- 사용자의 요청을 가장 먼저 받는 역할
- URL과 메서드(GET, POST 등)를 연결
- 요청 데이터와 응답 데이터를 관리
- Service와 클라이언트를 연결하는 중간 역할 수행
 */
