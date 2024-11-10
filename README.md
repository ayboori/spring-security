# 기능
- Spring Security Filter
    - JwtAuthenticationFilter : 로그인 검증
    - JwtAuthorizationFilter : 회원가입 / 로그인 이외의 API의 토큰 인증

- JWT Token 발행
  - access Token / Refresh Token 발행
  - access Token 만료 / Refresh Token 미 만료 시 **Refresh**
      - access Token, Refresh Token 새로 발행 후 헤더에 담기

- JUnit Test 작성
  - JWT Token 발행 테스트
 
- JwtToken의 role 체크를 위한 Post 작성, 조회
  - 글 작성 : `@PreAuthorize("hasRole('ROLE_ADMIN')")` 를 통해 ADMIN만 가능
  - 글 조회 : USER, ADMIN 모두 가능

- Swagger 적용
  - http://localhost:8080/swagger-ui/index.html
  - 아직 dev 환경에서만 확인 가능

# 배포
http://54.180.137.16:8080
위 주소 뒤에 API 주소로 postman Test 가능
