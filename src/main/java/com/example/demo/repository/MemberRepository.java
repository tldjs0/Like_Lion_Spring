package com.example.demo.repository;


import com.example.demo.domain.Member;

import java.util.List;

//회원 데이터 저장소 기능을 정의하는 인터페이스
//실제 DB 저장 방식은 구현 클래스에서 작성
public interface MemberRepository {
    void save(Member member); //회원 저장
    Member findById(Long id); //회원 id로 회원 조회
    List<Member> findAll(); //전체 회원 조회
    void remove(Long id); //회원 삭제
    Member findByUserId(String userId); //userId로 회원 조회
}

/*
MemberRepository 역할
- 회원 데이터를 저장하고 역할을 정의한 인터페이스
- 실제 데이터 저장 로직은 구현 클래스에서 작성
- Service 계층이 Repository를 통해 데이터를 관리

Interface를 사용하는 이유
인터페이스는 "기능의 규칙"만 정의
- save(Member member): 회원 정보를 저장하는 메서드
- findById(Long id): 회원 번호(id)로 회원 조회
- findAll(): 전체 회원 목록 조회
- remove(Long id): 회원 삭제
- findByUserId(String userId): 로그인 아이디(userId)로 회원 조회 | 로그인 기능에서 사용 가능

요청 흐름
Controller
-> Service
-> Repository
-> 데이터 저장/조회
 */