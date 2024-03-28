package com.healthmap.myway.member.controller;

import com.healthmap.myway.auth.TokenUserInfo;
import com.healthmap.myway.exception.ValidateEmailException;
import com.healthmap.myway.member.dto.request.MemberDeleteRequestDTO;
import com.healthmap.myway.member.dto.request.MemberModifyRequestDTO;
import com.healthmap.myway.member.dto.request.MemberSignInRequestDTO;
import com.healthmap.myway.member.dto.request.MemberSignUpRequsetDTO;
import com.healthmap.myway.member.dto.response.MemberInfoResponseDTO;
import com.healthmap.myway.member.dto.response.MemberModifyResponseDTO;
import com.healthmap.myway.member.dto.response.MemberSignInResponseDTO;
import com.healthmap.myway.member.dto.response.MemberSignUpResponseDTO;
import com.healthmap.myway.member.services.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Validated @RequestBody MemberSignUpRequsetDTO dto,
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
            MemberSignUpResponseDTO responseDTO = memberService.create(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    // 이메일 중복확인 요청처리
    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
        try {
            boolean flag = memberService.isDuplicateEmail(email);
            log.warn("{} 중복여부 - {}", email, flag);
            //log.info("client addr : {}", request.getRemoteAddr());
            return ResponseEntity.ok().body(flag);
        } catch (ValidateEmailException e) {
            return ResponseEntity.status(417).body(e.getMessage());
        }

    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Validated @RequestBody MemberSignInRequestDTO dto
    ) {
        try {
            MemberSignInResponseDTO responseDTO = memberService.authenticate(dto);
            //log.info("login success by {}", responseDTO.getUserName());
            return ResponseEntity.ok().body(responseDTO);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            //log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 프로필 사진 변경
    @PatchMapping("/change")
    public ResponseEntity<?> changeProfile(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestPart(name = "image") MultipartFile profileImg

    ) throws IOException {
        String s = memberService.uploadProfileImage(profileImg);

        MemberSignInResponseDTO change = memberService.change(userInfo, s);

        return ResponseEntity.ok().body(change);
    }

    // 내 정보 보기
    @GetMapping("/info")
    public ResponseEntity<?> info(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {

        log.info("정보조회");

        try {
            MemberInfoResponseDTO info = memberService.findUserInfo(userInfo);

            return ResponseEntity.ok().body(info);
        } catch (RuntimeException e) {
            log.warn("search error : {}", e.getMessage());
            return ResponseEntity.internalServerError().body("잘못된 정보입니다.");
        } catch (Exception e) {
            log.warn("/info - Error : {}", e.getMessage());
            return ResponseEntity.internalServerError().body("알 수 없는 오류가 발생했습니다.");
        }

    }

    // 회원정보 수정
    @PatchMapping("/modify")
    public ResponseEntity<?> modify(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody MemberModifyRequestDTO dto,
            BindingResult result
    ) {

        log.info("modify - userInfo : {}", userInfo);
        log.warn("modify - dto : {}", dto);
        if (result.hasErrors()) {
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }

        try {
            MemberModifyResponseDTO modify = memberService.modify(dto, userInfo);
            return ResponseEntity.ok().body(modify);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMember(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestBody MemberDeleteRequestDTO dto
    ) {
        try {
            memberService.delete(userInfo, dto);
            return ResponseEntity.ok("회원 탈퇴에 성공했습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(555).body("비밀번호가 틀렸습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("알 수 없는 오류가 발생했습니다.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        try {
            String profilePath = memberService.getProfilePath(userInfo.getEmail());

            if (!profilePath.endsWith("null")) {
                File profileFile = new File(profilePath);

                if (!profileFile.exists()) return ResponseEntity.notFound().build();

                byte[] fileData = FileCopyUtils.copyToByteArray(profileFile);

                HttpHeaders headers = new HttpHeaders();

                MediaType mediaType = extractFileExtension(profilePath);

                if (mediaType == null) {
                    return ResponseEntity.internalServerError().body("발견된 파일은 이미지가 아닙니다.");
                }

                headers.setContentType(mediaType);

                return ResponseEntity.ok().headers(headers).body(fileData);
            } else {
                return ResponseEntity.status(444).build();
            }


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private MediaType extractFileExtension(String filePath) {

        String ext = filePath.substring(filePath.lastIndexOf(".") + 1);

        switch (ext.toUpperCase()) {
            case "JPEG":
            case "JPG":
                return MediaType.IMAGE_JPEG;
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "GIF":
                return MediaType.IMAGE_GIF;
            default:
                return null;
        }
    }
}
