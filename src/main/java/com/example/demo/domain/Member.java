package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter //setter 메서드 자동 생성
@Getter //getter 메서드 자동 생성
@NoArgsConstructor //기본 생성자 자동 생성
public class Member {
    //필드
    private Long id; //회원 고유 번호
    private String userId;
    private String password;
    private String username;

    //회원 생성 시 필요한 값을 초기화하는 생성자
    public Member(String userId, String password, String username){
        this.userId = userId;
        this.password = password;
        this.username = username;
    }
}
/*Member 클래스는 회원 정보를 저장하는 Domain 객체
회원 한 명의 데이터를 하나의 객체로 표현
Controller -> Service -> Repository 계층을 이동할 때 회원 데이터를 담는 역할
*/