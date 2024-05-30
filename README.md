# ERD

![image](https://github.com/ydh5017/spring-personal-task-v2/assets/64765991/e5993d4b-f344-42ae-89bd-40a18c44fa89)


# API 명세서

| 기능 | API URL | Method | Request | Response |
| --- | --- | --- | --- | --- |
| 회원가입 | /api/user/signup | POST | 회원이름, 비밀번호, 이메일, 관리자 토큰 | login.html |
| 회워 정보 가져오기 | /api/user-info | GET | 회원 정보 | 회원이름, 관리자 여부 |
| 회원 폴더 정보 가져오기 | /api/user-folders | GET | 회원 정보 | 폴더 정보 목록 |
| jwt 유효성 확인 | /api/user/token-validation | GET | access 토큰 | access 토큰 상태 |
| 일정 등록 | /api/schedules | POST | 일정 제목, 내용, 파일 / 회원 정보 | 일정 정보 |
| 모든 일정 목록 조회 | /api/schedules/all | GET | 페이징 정보 | 페이징된 일정 목록 |
| 회원별 일정 목록 조회 | /api/schedules | GET | 페이징 정보, 회원정보 | 페이징된 일정 목록 |
| 일정 검색 조회 | /api/schedules/search | GET | 검색 키워드, 페이징 정보 | 페이징된 일정 목록 |
| 일정 수정 | /api/schedules/{id} | PUT | 일정 ID, 회원정보 | 일정 ID |
| 일정 삭제 | /api/schedules/{id} | DELETE | 일정 ID, 회원정보 | 일정 ID |
| 회원별 폴더에 일정 등록 | /api/schedules/{scheduleId}/folder | POST | 일정 ID, 폴더 ID, 회원정보 |  |
| 폴더에 등록된 일정 목록 조회 | /api/folders/{folderId}/schedules | GET | 폴더 ID, 페이징 정보, 회원정보 | 페이징된 일정 목록 |
| 폴더 등록 | /api/folders | POST | 폴더 이름 목록, 회원정보 |  |
| 폴더 목록 조회 | /api/folders | GET | 회원정보 | 폴더 목록 |
| 폴더 삭제 | /api/folders/{id} | DELETE | 폴더 ID |  |
| 일정에 저장된 파일 목록 조회 | /api/files | GET | 일정 ID | 파일 목록 |
| 파일 리소스 다운로드 | /api/files/download/{id} | GET | 파일 ID | 파일 리소스 |
| 댓글 등록 | /api/comments | POST | 일정ID, 댓글 내용 | 댓글 정보 |
| 댓글 목록 조회 | /api/comments | GET | 일정ID | 댓글 목록 |
| 댓글 수정 | /api/comments/{id} | PUT | 댓글ID, 내용, 회원정보 | 댓글 정보 |
| 댓글 삭제 | /api/comments/{id} | DELETE | 댓글ID, 회원정보 | 댓글 정보 |
