package com.sparta.springpersonaltaskv2.controller;

import com.sparta.springpersonaltaskv2.dto.SignupRequestDto;
import com.sparta.springpersonaltaskv2.dto.TokenDto;
import com.sparta.springpersonaltaskv2.dto.UserInfoDto;
import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import com.sparta.springpersonaltaskv2.security.UserDetailsImpl;
import com.sparta.springpersonaltaskv2.service.FolderService;
import com.sparta.springpersonaltaskv2.service.UserService;
import com.sparta.springpersonaltaskv2.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final FolderService folderService;
    private final JwtUtil jwtUtil;

    @GetMapping("/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/user/signup")
    public String signupPage() {
        return "signup";
    }

    /**
     * 회원가입
     * @param requestDto 회원 등록 정보
     * @param bindingResult Validation 예외 에러
     * @return login 페이지 반환, 에러 발생 시 signup 페이지 반환
     */
    @PostMapping("/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    /**
     * 회원 관련 정보 받기
     * @param userDetails 회원 정보
     * @return 회원 정보
     */
    @GetMapping("/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUser().getUsername();
        UserRoleType role = userDetails.getUser().getRole();
        boolean isAdmin = (role == UserRoleType.ADMIN);

        return new UserInfoDto(username, isAdmin);
    }

    /**
     * 회원 폴더 목록 조회
     * @param model 회원 폴더 목록 담을 model
     * @param userDetails 회원정보
     * @return index 페이지
     */
    @GetMapping("/user-folder")
    public String getUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("folders", folderService.getFolders(userDetails.getUser()));
        return "index :: #fragment";
    }

    /**
     * access 토큰 유형성 검사
     * @param accessToken 토큰
     * @return 토큰 상테 true or false
     */
    @GetMapping("/user/token-validation")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> tokenValidation(@RequestParam("accessToken") String accessToken) {
        boolean valid = jwtUtil.validateToken(accessToken.substring(7));
        Map<String, Boolean> response = new HashMap<>();
        response.put("status", valid);
        return ResponseEntity.ok(response);
    }

    /**
     * 토큰 재발급
     * @param refreshToken 토큰
     * @param req http 요청 객체
     * @return 토큰
     */
    @GetMapping("/user/token-reissuance")
    @ResponseBody
    public ResponseEntity<?> tokenReissuance(@RequestParam("refreshToken") String refreshToken, HttpServletRequest req) {
        TokenDto tokenDto = userService.tokenReissuance(refreshToken, req);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", tokenDto.getGrantType() + tokenDto.getAccessToken());
        response.put("refreshToken", tokenDto.getGrantType() + tokenDto.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}