package com.example.demo.repository;

import com.example.demo.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j // 로그 출력 기능 사용
@Repository // Spring이 Repository 객체로 관리하도록 등록
public class MemberRepositoryImpl implements MemberRepository{

    // JSON 변환 객체 | 자바 객체 <-> JSON 변환 역할
    private final ObjectMapper objectMapper = new ObjectMapper();
    // 회원 데이터를 저장할 JSON 파일 경로
    private final String DATA_FILE_PATH = "data/members.json";
    // 회원 데이터를 메모리에 저장하는 Map
    // key = 회원 id
    // value = Member 객체
    private final Map<Long, Member> store = new ConcurrentHashMap<>();

    // 회원 id 자동 증가 값
    private final AtomicLong sequence = new AtomicLong(0L);

    // Repository 생성 시 파일 데이터 로드
    public MemberRepositoryImpl(){
        loadDataFromFile();
    }

    // JSON 파일의 회원 데이터를 메모리로 불러오는 메서드
    private void loadDataFromFile(){
        File file = new File(DATA_FILE_PATH); // 파일 객체 생성
        if(!file.exists()){ // 파일 존재 여부 확인
            List<Member> members = objectMapper.readValue(file, new TypeReference<List<Member>>() {
            }); // JSON 파일을 List<Member> 형태로 읽기
            for(Member member: members){ // 회원 데이터를 Map(store)에 저장
                store.put(member.getId(), member); // id를 key로 저장
                if(member.getId() > sequence.get()){ //가장 큰 id 기준으로 sequence 값 증가
                    sequence.set(member.getId() + 1);
                }
            }
            log.info("회원 데이터 로드 완료 : {}명", members.size()); // 로그 출력
        }
        else { //파일이 없으면 data 폴더 생성
            File directory = new File("data");
            if(!directory.exists()){ // data 폴더 존재 여부 확인
                directory.mkdirs(); // 폴더 생성
            }
            log.info("기존 데이터 파일이 없어 새로 시작합니다.");
        }
    }

    // 현재 회원 데이터를 JSON 파일에 저장
    private void saveDataToFile() {
        try{
            List<Member> members = new ArrayList<>(store.values()); // Map 값을 리스트로 변환
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(DATA_FILE_PATH), members); //JSON 파일 저장
        } catch (JacksonException e) {
            throw new RuntimeException(e); // 예외 발생 시 런타임 예외 처리
        }
    }

    @Override // 회원 저장 메서드
    public void save(Member member){
        if(member.getId() == null){ // 신규 회원 저장
            long newId = sequence.incrementAndGet(); // 새로운 id 생성
            member.setId(newId); // 회원 id 설정
            store.put(newId, member); // Map에 저장
        }else{
            store.put(member.getId(), member); // 기존 회원 수정
        }
        saveDataToFile(); // 파일 저장
    }

    @Override // id로 회원 조회
    public Member findById(Long id){ return store.get(id); }

    @Override // 전체 회원 조회
    public List<Member> findAll() { return new ArrayList<>(store.values()); }

    @Override // 회원 삭제
    public void remove(Long id){
        store.remove(id); // Map에서 삭제
        saveDataToFile(); // 파일 저장
    }

    @Override // userId로 회원 조회
    public Member findByUserId(String userId){
        return store.values().stream()
                .filter(member -> member.getUserId().equals(userId)) // userId가 같은 회원 찾기
                // 아무 회원 1명 반환
                .findAny()
                // 없으면 null 반환
                .orElse(null);
    }
}

/*
MemberRepositoryImpl 역할
- MemberRepository 인터페이스를 실제 구현한 클래스
- 회원 데이터를 저장, 조회, 수정, 삭제하는 기능 수행
- 데이터를 메모리와 JSON 파일에 저장

주요 기능

store
- 회원 데이터를 메모리에 저장하는 공간
- key는 회원 id
- value는 Member 객체

sequence
- 회원 id 자동 증가 기능
- 회원 저장 시 새로운 번호 생성

loadDataFromFile()
- 프로그램 실행 시 JSON 파일 데이터를 읽어 메모리에 저장
- 기존 회원 데이터를 유지하기 위해 사용

saveDataToFile()
- 현재 회원 데이터를 JSON 파일에 저장
- 프로그램 종료 후에도 데이터 유지 가능

save()
- 회원 저장 및 수정 기능
- id가 없으면 신규 회원 저장
- id가 있으면 기존 회원 수정

findByUserId()
- 로그인 시 아이디로 회원 조회
- stream을 사용해 userId가 같은 회원 검색

요청 흐름
Controller
-> Service
-> RepositoryImpl
-> Map 저장
-> JSON 파일 저장
 */
