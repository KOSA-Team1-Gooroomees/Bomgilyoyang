# 봄길요양(Bomgilyoyang-Spring-MVC)

> "늘 봄날 같은 산책을 위한 요양시설 선택 지도 서비스”

<img width="1448" height="1086" alt="메인페이지" src="https://github.com/user-attachments/assets/805e369f-2f29-4faa-b42d-f2c781e178bf" />




## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [프로젝트 기획 배경](#2-프로젝트-기획-배경)
3. [구성원 및 역할](#3-구성원-및-역할)
4. [기술 스택](#4-기술-스택)
5. [주요 기능 및 서비스 화면](#5-주요-기능-및-서비스-화면)
6. [프로젝트 구조](#6-프로젝트-구조)
7. [협업 컨벤션](#7-협업-컨벤션-브랜치-전략)
8. [트러블 슈팅](#8-트러블-슈팅)

---

### 1. 프로젝트 개요

* **프로젝트명**: 봄길요양 (BomgilYoyang)
* **한 줄 설명**: 공원과 요양시설 정보를 기반으로 사용자에게 적합한 시설을 추천하는 지도 서비스
* **진행 기간(Spring MVC)**: 2026.05.06 ~ 2026.05.13
* **개발 인원**: 풀스택 개발 4인
* **팀명**: Gooroomees
* **GitHub**: https://github.com/KOSA-Team1-Gooroomees/Bomgilyoyang-Spring-MVC

---

### 2. 프로젝트 기획 배경

고령화 사회가 빠르게 진행되면서 요양시설과 복지시설에 대한 관심과 수요는 꾸준히 증가하고 있습니다. 하지만 실제 이용자와 보호자는 시설의 위치, 종류, 기본 정보뿐만 아니라 주변 환경까지 함께 고려해야 하기 때문에 적합한 시설을 선택하는 데 어려움이 있습니다.

특히 어르신들의 일상에서 산책 환경, 공원 접근성, 주변 편의시설은 생활 만족도와 정서적 안정에 영향을 줄 수 있는 중요한 요소입니다. 그러나 기존 시설 검색 서비스는 시설 정보 중심으로 제공되는 경우가 많아, 주변 환경을 함께 비교하며 시설을 선택하기에는 한계가 있었습니다.

봄길요양은 이러한 문제를 해결하기 위해 **공원과 복지시설 정보를 함께 제공하고, 주변 환경을 기반으로 사용자에게 적합한 시설을 추천하는 지도 서비스**로 기획되었습니다.

---

### 3. 구성원 및 역할

#### 구성원

| <img width="120" height="160" alt="우지연" src="https://github.com/user-attachments/assets/3f702150-b275-44d2-bd1e-64f6324ab752" /> | <img width="120" height="160" alt="신재욱" src="https://github.com/user-attachments/assets/4bfd7a98-b272-4c54-902c-2eb7daaf4495" /> | <img width="120" height="160" alt="이민호" src="https://github.com/user-attachments/assets/f1df5f47-442e-4e67-8488-291bb30361c5" /> | <img width="120" height="160" alt="조윤지" src="https://github.com/user-attachments/assets/1049de8e-c180-4c53-b447-248fc5a30dd1" /> |
| :---: | :---: | :---: | :---: |
| 우지연 | 신재욱 | 이민호 | 조윤지 |
| [GitHub](https://github.com/dnwldus410) | [GitHub](https://github.com/tls427wodnr) | [GitHub](https://github.com/minho2618) | [GitHub](https://github.com/YUMZII) |

#### 역할

| 이름  | 역할 | 개발 파트                      |
| --- | -- | -------------------------- |
| 우지연 | 팀장 | 실시간 채팅(WebSocket), 관리자 페이지 |
| 신재욱 | 팀원 | 형상관리, 지도 기능, 시설 검색 기능      |
| 이민호 | 팀원 | 회원 및 인증                    |
| 조윤지 | 팀원 | 게시판                        |

---

### 4. 기술 스택

| 구분               | 기술                                       |
| ---------------- | ---------------------------------------- |
| Language         | Java, JavaScript, HTML5, CSS3            |
| Backend          | Spring Boot, Spring MVC, Spring Security |
| Template Engine  | Thymeleaf                                |
| Database         | MySQL                                    |
| ORM              | Spring Data JPA                          |
| Realtime         | WebSocket, STOMP, SockJS                 |
| Build Tool       | Gradle                                   |
| Styling          | Tailwind CSS, CSS                        |
| Version Control  | Git, GitHub                              |
| Collaboration    | GitHub, Notion, Figma                    |
| Development Tool | IntelliJ IDEA                            |

---

### 5. 주요 기능 및 서비스 화면

<details>
<summary><strong>회원 기능</strong></summary>

<br>

* 일반 회원가입 및 카카오 OAuth 로그인을 지원합니다.
* 이메일 인증을 통해 회원가입 시 사용자 계정의 유효성을 확인합니다.
* Spring Security 기반으로 로그인, 로그아웃, 마이페이지 조회 기능을 제공합니다.
* 일반 회원은 비밀번호 수정 및 회원 탈퇴를 진행할 수 있습니다.

<br>

<img width="280" alt="회원가입_1_과정" src="https://github.com/user-attachments/assets/c3b4d100-e216-4c13-a03b-770c833eeb25" />
<img width="280" alt="회원가입_2_완료" src="https://github.com/user-attachments/assets/f75e393a-618b-4270-8e96-e9b2e8927951" />
<img width="280" alt="마이페이지_1_수정" src="https://github.com/user-attachments/assets/5472c6bf-20cf-4beb-8884-b258a654d16c" />
<br>
<img width="500" alt="회원가입_4_이메일" src="https://github.com/user-attachments/assets/9d15c0a2-76ee-4160-85f8-8590659382be" />

</details>

<details>
<summary><strong>지도 / 검색 기능</strong></summary>

<br>

* 요양시설을 지역 기반으로 검색할 수 있습니다.
* 지도에서 요양시설의 위치와 상세 정보를 확인할 수 있습니다.
* 선택한 시설 주변의 공원 정보를 함께 제공하여 주변 공원 환경을 확인할 수 있습니다.
* 로그인 사용자는 관심 시설을 즐겨찾기에 등록하고 관리할 수 있습니다.

<br>

<img src="https://github.com/user-attachments/assets/c6686a34-3b45-44b8-88ae-ee5a90b88244" width="800" />
<br>
<img src="https://github.com/user-attachments/assets/9f15eb66-32ba-4872-83e3-cb925d0d2269" width="300" height="500" />
<img src="https://github.com/user-attachments/assets/d08b8050-5348-45b7-9c3b-09c00feb44c9" width="500" height="500" />

</details>

<details>
<summary><strong>게시판 기능</strong></summary>

<br>

* 로그인한 사용자는 게시글을 작성, 수정, 삭제할 수 있습니다.
* 게시글 작성 시 파일을 첨부할 수 있으며, 첨부 파일 다운로드를 지원합니다.
* 댓글 조회와 좋아요 기능을 통해 사용자 간 정보 공유와 소통이 가능합니다.

<br>

<img src="https://github.com/user-attachments/assets/6b4d3b4b-a6bc-4734-9a17-dc02c58d55e8" width="1000" />
<br>
<img src="https://github.com/user-attachments/assets/ae83a244-0f7b-4c04-a635-3b7f8fb4425f" width="370" height="350" align="left" />
<img src="https://github.com/user-attachments/assets/5f83b2a0-7d01-453a-8649-8f26352c8c57" width="600" height="350" />
</details>

<details>
<summary><strong>실시간 채팅 기능</strong></summary>

<br>

* 사용자는 관리자와 실시간으로 채팅을 진행할 수 있습니다.
* WebSocket, STOMP 기반으로 사용자와 관리자 간 메시지를 실시간 송수신합니다.
* 채팅 메시지는 데이터베이스에 저장되어 이전 대화 내역을 확인할 수 있습니다.
* 읽지 않은 메시지가 있는 경우 사용자 채팅 버튼과 관리자 채팅 목록에 알림을 표시합니다.

<br>

<img width="250" alt="채팅_1_안읽음" src="https://github.com/user-attachments/assets/7dcd3ddf-ea79-4ca4-b373-78c8f15a76ba" />
<img width="250" alt="채팅_2_읽음" src="https://github.com/user-attachments/assets/5cb05423-c756-49c9-a272-0490637e62d4" />

</details>

<details>
<summary><strong>관리자 기능</strong></summary>

<br>

* 관리자는 전체 사용자와 탈퇴 사용자를 구분하여 조회할 수 있습니다.
* 사용자별 채팅방 목록을 확인하고, 선택한 채팅방에서 사용자 문의에 응답할 수 있습니다.
* 사용자 상태를 확인하고 관리할 수 있습니다.

<br>

<img width="750" alt="관리자페이지_1_전체" src="https://github.com/user-attachments/assets/3aabc50a-7c22-41e1-ada6-7a7de2f5157b" />
<img width="320" alt="관리자페이지_2_채팅" src="https://github.com/user-attachments/assets/8def1294-f637-455f-91e6-40b8f9afd391" />

</details>

---

### 6. 프로젝트 구조

<details>
<summary><strong>Tree 구조 보기</strong></summary>

<br>

```text
src/main
├── java
│   └── com.gooroomees.neulbomgil_backend
│       ├── domain
│       │   ├── admin #관리자
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   └── service
│       │   │
│       │   ├── auth #회원
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   │   ├── request
│       │   │   │   └── response
│       │   │   ├── entity
│       │   │   ├── repository
│       │   │   └── service
│       │   │
│       │   ├── board #게시판
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   ├── entity
│       │   │   ├── repository
│       │   │   └── service
│       │   │
│       │   ├── chat #채팅
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   ├── entity
│       │   │   ├── repository
│       │   │   └── service
│       │   │
│       │   ├── common
│       │   │
│       │   ├── favorite #즐겨찾기
│       │   │   ├── controller
│       │   │   ├── dto
│       │   │   │   ├── request
│       │   │   │   └── response
│       │   │   ├── entity
│       │   │   ├── repository
│       │   │   └── service
│       │   │
│       │   ├── home
│       │   │   └── controller
│       │   │
│       │   ├── map #지도
│       │   │   ├── controller
│       │   │   │   └── api
│       │   │   ├── dto
│       │   │   │   ├── request
│       │   │   │   └── response
│       │   │   ├── entity
│       │   │   ├── repository
│       │   │   └── service
│       │   │
│       │   └── reply #댓글
│       │       ├── controller
│       │       ├── dto
│       │       ├── entity
│       │       ├── repository
│       │       └── service
│       │
│       └── global
│           └── config
│
└── resources
    ├── data
    ├── db
    │   └── seed
    ├── static
    │   ├── css
    │   ├── fonts
    │   ├── images
    │   │   ├── characters
    │   │   ├── facility
    │   │   └── icons
    │   └── js
    └── templates
        ├── auth
        ├── board
        ├── chat
        ├── common
        ├── fragments
        ├── grade
        └── map
```

</details>

---

### 7. 협업 컨벤션 (브랜치 전략)

<details>
<summary><strong>1. 브랜치 전략 선정 배경</strong></summary>

<br>

* 프로젝트 기간이 비교적 짧은 미니 프로젝트 특성상 Git-flow를 적용하기에는 브랜치 관리 비용이 크다고 판단하였습니다.
* 반면 GitHub-flow는 빠른 개발이 가능하지만 검증 단계가 부족하여 코드 안정성 측면에서 리스크가 존재하였습니다.
* 따라서 우리 팀은 GitHub-flow의 간결함과 Git-flow의 안정성을 적절히 결합한 **main - develop - feature 브랜치 전략**을 채택하였습니다.

</details>

<details>
<summary><strong>2. 브랜치 구조 및 역할</strong></summary>

<br>

#### main

* 최종 배포가 가능한 안정적인 코드를 관리하는 브랜치입니다.
* 언제든 서비스가 가능한 상태를 유지합니다.

#### develop

* 팀원들의 작업 결과가 통합되는 브랜치입니다.
* 기능 검증 및 충돌 확인을 수행하는 통합 브랜치입니다.
* 검증이 완료된 코드만 main으로 반영합니다.

#### feature

* 실제 기능 개발이 이루어지는 브랜치입니다.
* develop 브랜치에서 생성합니다.
* 작업 완료 후 develop 브랜치로 병합합니다.

</details>

<details>
<summary><strong>3. 이슈 기반 브랜치 관리</strong></summary>

<br>

* 기능 개발 전 GitHub Issue를 먼저 생성하였습니다.
* 생성된 Issue 번호를 기반으로 feature 브랜치를 생성하여 작업 내역과 이슈를 연결하였습니다.

#### 예시

```text
Issue #12 생성
        ↓
feat/#12-login
```

```text
Issue #27 생성
        ↓
fix/#27-chat
```

</details>

<details>
<summary><strong>4. 워크플로우 (Workflow)</strong></summary>

<br>

```text
1. Issue 생성
        ↓
2. develop 브랜치에서 feature 브랜치 생성
        ↓
3. 기능 개발 및 Commit
        ↓
4. 원격 feature 브랜치 Push
        ↓
5. Pull Request 생성
        ↓
6. 코드 리뷰 및 승인
        ↓
7. develop 브랜치 Merge
        ↓
8. 최종 검증 후 main Merge
```

</details>

<details>
<summary><strong>5. 운영 원칙 (Ground Rules)</strong></summary>

<br>

* 모든 기능 개발은 반드시 feature 브랜치에서 진행합니다.
* 직접 main 또는 develop 브랜치에 작업하지 않습니다.
* 기능 구현 전 반드시 Issue를 생성합니다.
* 브랜치명은 Issue 번호 기반으로 작성합니다.
* 모든 코드는 Pull Request를 통해서만 상위 브랜치에 병합합니다.
* 리뷰가 용이하도록 작은 단위로 작업을 나누어 PR을 생성합니다.
* Issue 및 Pull Request는 팀에서 정의한 템플릿을 사용합니다.
* feature → develop 병합 시 최소 2명의 리뷰를 진행합니다.
* develop → main 병합 시 팀원 전원의 검토 후 PR을 올린 팀원을 제외한 나머지 3명의 리뷰를 작성한 뒤 병합합니다.

</details>

---

### 8. 트러블 슈팅

<details>
<summary><strong>1. 아키텍처 개선: 실시간 API 호출 방식을 배치 동기화로 전환</strong></summary>


#### 비즈니스 로직의 타당성
* **기존 구조의 한계**: 변동성이 낮은 데이터를 매 검색 요청마다 외부 API에서 호출하여 불필요한 네트워크 비용이 누적되고, 외부 장애 발생 시 서비스 전체가 마비되는 리스크 존재.
* **이원화 동기화 전략**:
  * **요양시설 데이터 (동적)**: 정보 변경이 비교적 잦아 최신성 유지를 위해 **매일 새벽 4시 외부 API 기반 배치 작업** 수행.
  * **공원 데이터 (정적)**: 변동이 거의 없는 특성을 고려하여 **로컬 JSON 파일로 관리하며 애플리케이션 초기 기동 시 1회만 적재**하여 인프라 효율 극대화.
* **기대 효과**: 외부 의존성 제거를 통한 고가용성(HA) 확보 및 로컬 DB 인덱싱을 활용한 일관되고 빠른 검색 속도 보장.

#### 기술적 도전 과제 및 해결 방안
* **대량 시설 데이터 적재 시 네트워크 I/O 오버헤드**:
  * *문제점*: 루프 내에서 단건 데이터마다 `findById` 및 `save`를 호출하여 네트워크 Latency 누적.
  * *해결방안*: `findAllById`를 도입하여 **IN 절을 통한 1회 쿼리**로 기존 데이터를 일괄 조회 후 Map으로 변환하고, `saveAll`을 활용해 DB I/O 요청 횟수 최소화.
* **대량 공원 데이터 초기화 시 Memory Leak 및 벌크 인서트 미작동**:
  * *문제점*: JPA `IDENTITY` 전략 사용으로 인해 `saveAll` 호출 시 Bulk Insert가 작동하지 않고 단건 Insert가 수만 번 발생하며, 수만 건의 전체 데이터를 한 번에 엔티티 리스트로 적재하여 `OOM(Out Of Memory)` 위험 노출.
  * *해결방안*: **JdbcTemplate**의 `batchUpdate`를 활용해 실제 벌크 인서트를 구현하고, 데이터를 **1,000건 단위(Chunk)로 분할 처리** 후 즉시 메모리를 비워 일정 수준의 메모리 점유율 유지.

</details>

<details>
<summary><strong>2. N+1 문제 트러블슈팅</strong></summary>


#### 문제
* 관리자 채팅방 목록 조회 시, 채팅방 목록을 먼저 조회한 뒤 반복문 안에서 각 채팅방의 사용자 정보와 최신 메시지를 가져오는 구조로 인해 N+1 문제 발생.
* 특히 최신 메시지를 채팅방마다 개별 조회하면서 채팅방 수가 증가할수록 추가 쿼리가 반복 실행되어 성능 저하 야기.

#### 해결 방법
* 1차적으로 `Fetch Join`을 적용하여 `ChatRoom`과 `User`를 한 번에 묶어서 조회하도록 처리했으나, 최신 메시지 조회의 반복 쿼리 문제는 여전히 잔존함.
* 최종적으로 JPQL을 사용하여 채팅방 정보, 사용자 정보, 최신 메시지를 한 번에 프로젝션하여 DTO로 조회하도록 로직을 수정, 단일 쿼리로 최적화 완료.

#### 결과
* 채팅방 100개 기준 실행 시간 비교: **181ms (기존) ➔ 123ms (Fetch Join) ➔ 34ms (JPQL DTO 조회)**
* 단일 쿼리 변환을 통해 대량의 목록 조회 시에도 안정적이고 빠른 응답 속도 확보.

</details>

<details>
<summary><strong>3. 세션 방식 인증에서 카카오 OAuth 2.0 도입 전환</strong></summary>


#### 문제
* 기존 서버 메모리 및 세션(`HttpSession`) 기반의 인증 환경에 카카오 소셜 로그인을 추가하는 과정에서, 카카오 인증 정보(Access Token 및 프로필)가 기존 세션 처리 흐름과 결합되지 않아 로그인 세션이 유지되지 않거나 유저 정보가 누락되는 현상 발생.

#### 원인
* **인증 메커니즘의 불일치**: 카카오 OAuth는 [인가 코드 발급 ➔ 토큰 교환 ➔ 프로필 조회] 순의 API 기반 흐름인 반면, 기존 방식은 Form Login 기반으로 Spring Security 필터가 직접 세션을 생성하므로 두 흐름 간 접점 부재.
* **식별자 매핑 부재**: 카카오 고유 식별자(`Provider ID`)를 기존 회원 DB의 ID 체계와 연동할 매핑 테이블 또는 컬럼이 설계되지 않아 신규/기존 회원 판별 불가.

#### 해결 방안
* **OAuth2 로그인 성공 후 세션 강제 연동**: Spring Security OAuth2 Client 라이브러리를 활용하거나 카카오 API 호출 성공 후 반환된 사용자 정보를 바탕으로 서비스 내부의 `CustomUserDetails` 생성.
* **SecurityContext 수동 등록**: 이후 `SecurityContextHolder.getContext().setAuthentication(authentication)`을 통해 인증 객체를 컨텍스트에 수동 등록함으로써, Spring Security가 기존 방식대로 세션(`JSESSIONID`)을 정상 발급 및 유지하도록 처리.
* **회원 엔티티 확장**: DB 회원 테이블에 `provider`(예: KAKAO, LOCAL) 및 `provider_id` 컬럼을 추가하여, 최초 소셜 로그인 시 자동으로 회원가입 처리(`JIT: Just-In-Time Provisioning`)가 되도록 로직 정비.

</details>

<details>
<summary><strong>4. React-Vite 환경에서의 이미지 CORS 에러 트러블슈팅</strong></summary>

#### 문제
* 게시글 작성 시 업로드한 이미지가 상세 페이지에 전혀 표시되지 않는 현상 발생.
* 파일 업로드 기능 자체는 정상 동작하며 데이터베이스에도 파일 경로가 올바르게 저장되어 있어, 초기 원인 파악에 어려움이 존재함.

#### 원인
* **브라우저 CORS(Cross-Origin Resource Sharing) 정책 위반**: 프론트엔드(React/Vite: `5173` 포트)와 백엔드(Spring Boot: `8088` 포트)의 출처(Origin)가 서로 달라, 브라우저가 보안상의 이유로 타 출처의 이미지 리소스 요청을 차단함.

#### 해결 방안
* **Vite 프록시(Proxy) 설정 적용**: 개발 환경에서 `/api` 및 이미지 리소스 요청을 Vite 개발 서버가 중간에서 가로채 백엔드 서버(`8088`)로 포워딩하도록 설정.
* 이를 통해 브라우저 입장에서는 모든 요청이 동일한 출처(`5173`)로 향하는 것처럼 인식하게 만들어 CORS 정책을 우회하고 문제를 해결함.

</details>
