package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.utils.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Service 계층으로 등록 | 비즈니스 로직을 처리하는 클래스
@RequiredArgsConstructor // final 필드를 사용하는 생성자 자동 생성
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository; // 회원 데이터 처리 Repository
    private final JwtUtil jwtUtil; // JWT 토큰 처리 유틸 클래스

    public Member tokenToMember(String token){ // JWT 토큰을 이용해 회원 객체 조회
        return memberRepository.findByUserId(jwtUtil.getClaimsFromJwt(token).getSubject());
    } // 토큰 안의 userId(subject)를 추출하여 회원 조회

    @Transactional
    public Long signUp(Member member){
        String hashedPassword = BCrypt.hashpw(member.getPassword(), BCrypt.gensalt());//BCrypt 암호화하는 애
        member.setPassword(hashedPassword); // 암호화된 비밀번호 저장
        memberRepository.save(member); // 회원 저장
        return member.getId(); // 저장된 회원 id 반환
    }

    public List<Member> findAll() { return memberRepository.findAll(); } // 전체 회원 조회

    public Member findById(Long memberId) { return memberRepository.findById(memberId); } // 회원 id로 회원 조회

    @Transactional
    public void update(Long id, String newName, String newPassword){ // 회원 정보 수정
        Member member = memberRepository.findById(id); // id로 회원 조회

        member.setUsername(newName); // 회원 이름 수정
        if(newPassword != null && !newPassword.isEmpty()){ // 새 비밀번호 값이 존재할 경우만 수정
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt()); // 새 비밀번호 암호화
            member.setPassword(hashedPassword); // 암호화된 비밀번호 저장
        }

        memberRepository.save(member); // 수정된 회원 정보 저장
    }

    @Transactional
    public void delete(Long id) { memberRepository.remove(id); } // 회원 삭제

    public String login(String userId, String password) { // 로그인 기능
        Member member = memberRepository.findByUserId(userId); // userId로 회원 조회
        if(member != null && BCrypt.checkpw(password, member.getPassword())){ // 회원 존재 여부와 비밀번호 일치 여부 확인
            String token = jwtUtil.generateJwt(member.getUserId(), member.getUsername()); // 로그인 성공 시 JWT 토큰 생성
            return token;
        }
        return "아이디와 비밀번호를 확인하세요"; // 로그인 실패 메시지 반환
    }

    // userId로 회원 조회
    public Member findByUserId(String userId) { return memberRepository.findByUserId(userId); }

}
