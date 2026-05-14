package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberDTO {

    /*기본 생성자를 막아 객체 생성 방지
    DTO는 데이터를 담는 용도로만 사용하기 때문에 객체 생성 제한
    Create, Login, Member 등 DTO 내부 클래스들을 묶어두는 용도로만 사용
    그래서 실수로 객체를 생성하지 못하게 private MemberDTO()로 생성자를 private 처리한 것
    Java는 아무 생성자도 안 만들면 자동으로 기본 생성자를 만들어줌 public MemberDTO(){}
    private MemberDTO() 생성자는 클래스 내부에서만 사용할 수 있다. 즉 외부 접근 불가능
    => 외부에서 실수로 객체 생성하는 걸 막기 위한 코드
    */
    private MemberDTO(){

    }

    //요청 관련 DTO들을 모아둔 클래스
    public static class Request{


        @Data //getter, setter, toString 등 자동 생성
        @NoArgsConstructor
        public static class Create{//회원가입 요청 DTO
            private String userId;
            private String password;
            private String username;
        }

        @Data
        @NoArgsConstructor
        public static class Update{//회원정보 수정 요청 DTO
            private String username;//수정할 이름
            private String password;//수정할 비밀번호
        }

        @Data
        @NoArgsConstructor
        public static class Login{//로그인 요청 DTO
            private String userId;
            private String password;
        }
    }

    public static class Response {//응답 관련 DTO들을 모아둔 클래스

        @Data
        @AllArgsConstructor//모든 필드를 사용하는 생성자 자동 생성
        public static class Member {//회원 정보 응답 DTO
            private Long id;
            private String userId;
            private String username;
        }
    }

    //공통 응답 형식을 만들기 위한 DTO
    @Data
    @AllArgsConstructor
    public static class Result<T> {
        private T data; //실제 응답 데이터를 저장
    }
}/*T는 어떤 타입이든 들어올 수 있는 제네릭,
- 회원 정보도 담을 수 있고
- 리스트도 담을 수 있고
- 문자열도 담을 수 있음
Result<T>는 서버가 보내는 응답 데이터를 일정한 형태로 묶어서 반환하기 위한 클래스

MemberDTO의 역할
DTO는 계층 간 데이터를 전달하기 위한 객체이다.
Controller와 Service 사이에서 요청값과 응답값을 전달할 때 사용
Entity를 직접 노출하지 않고 필요한 데이터만 전달하기 위해 사용

Request: 클라이언트가 서버로 보내는 요청 데이터를 저장
- Create: 회원가입 요청
- Update: 회원정보 수정 요청
- Login: 로그인 요청

Response: 서버가 클라이언트에게 보내는 응답 데이터를 저장
- Member: 회원 정보를 응답할 때 사용

Result<T>: 응답 데이터를 공통 형식으로 감싸기 위한 클래스

왜 DTO를 사용하는가?
- 필요한 데이터만 전달 가능
- 보안상 Entity 직접 노출 방지
- 계층 간 역할 분리 가능
- 유지보수가 쉬원짐

요청 흐름
회원가입 요청이 들어오면:
Controller
-> MemberDTO.Request.Create //회원가입 요청
-> Service
-> Repository 저장
-> MemberDTO.Response.Member(domain)
-> 사용자에게 응답

Controller <(DTO)> Service
*/