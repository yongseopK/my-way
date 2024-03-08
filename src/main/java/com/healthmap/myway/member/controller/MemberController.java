package com.healthmap.myway.member.controller;


import com.healthmap.myway.auth.TokenUserInfo;
import com.healthmap.myway.member.dto.request.MemberModifyRequestDTO;
import com.healthmap.myway.member.dto.request.MemberSignInRequestDTO;
import com.healthmap.myway.member.dto.request.MemberSignUpRequsetDTO;
import com.healthmap.myway.member.dto.response.MemberModifyResponseDTO;
import com.healthmap.myway.member.dto.response.MemberSignInResponseDTO;
import com.healthmap.myway.member.dto.response.MemberSignUpResponseDTO;
import com.healthmap.myway.member.services.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/test")
    public String testAPI() {
        return "안녕";
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Validated @RequestPart("user") MemberSignUpRequsetDTO dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImg,
            BindingResult resule
    ) {

        // 요청이 정상적으로 들어오는지 로그 출력
        log.info("member POST!! - {}", dto);

        // 에러처리
        if (resule.hasErrors()) {
            log.warn(resule.toString());
            return ResponseEntity.badRequest().body(resule.getFieldError());
        }

        try {
            String uploadProfileImagePath = null;
            if (profileImg != null) {
                log.info("original image name : {}", profileImg.getOriginalFilename());
                uploadProfileImagePath = memberService.uploadProfileImage(profileImg);
            }
            MemberSignUpResponseDTO responseDTO = memberService.create(dto, uploadProfileImagePath);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Validated @RequestBody MemberSignInRequestDTO dto
    ) {
        try {
            MemberSignInResponseDTO responseDTO = memberService.authenticate(dto);
            log.info("login success by {}", responseDTO.getUserName());
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원정보 수정
    @PatchMapping("/modify")
    public ResponseEntity<?> modify(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestPart(value = "member")MemberModifyRequestDTO dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImg,
            BindingResult result
            ) {

        log.info("modify - userInfo : {}", userInfo);
        if(result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }

        try {
            String uploadProfileImagePath = null;
            if(profileImg != null) {
                log.info("file-name : {}", profileImg.getOriginalFilename());
                uploadProfileImagePath = memberService.uploadProfileImage(profileImg);
            }
            MemberSignInResponseDTO modify = memberService.modify(dto, userInfo, uploadProfileImagePath);
            return ResponseEntity.ok().body(modify);
        } catch (IllegalStateException e){
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
