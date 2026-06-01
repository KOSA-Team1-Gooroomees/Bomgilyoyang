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

추가 예정
