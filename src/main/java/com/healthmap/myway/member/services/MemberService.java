package com.healthmap.myway.member.services;

import com.healthmap.myway.auth.TokenProvider;
import com.healthmap.myway.auth.TokenUserInfo;
import com.healthmap.myway.member.dto.request.MemberModifyRequestDTO;
import com.healthmap.myway.member.dto.request.MemberSignInRequestDTO;
import com.healthmap.myway.member.dto.request.MemberSignUpRequsetDTO;
import com.healthmap.myway.member.dto.response.MemberModifyResponseDTO;
import com.healthmap.myway.member.dto.response.MemberSignInResponseDTO;
import com.healthmap.myway.member.dto.response.MemberSignUpResponseDTO;
import com.healthmap.myway.member.entity.Member;
import com.healthmap.myway.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Value("${upload.path}")
    private String rootPath;


    // 회원가입 처리
    public MemberSignUpResponseDTO create(MemberSignUpRequsetDTO dto, String profilePath) {
        if (dto == null) {
            throw new RuntimeException("회원가입 입력정보가 없습니다.");
        }
        String email = dto.getEmail();

        if (memberRepository.existsByEmail(email)) {
            log.warn("이메일이 중복되었습니다. : {}", email);
            throw new RuntimeException("중복된 이메일입니다.");
        }

        Member save = memberRepository.save(dto.toEntity(passwordEncoder, profilePath));

        log.info("회원가입 성공 saved user - {}", save.getUserName());

        return new MemberSignUpResponseDTO(save);
    }

    // 이메일 중복체크 함수
    public boolean isDuplicateEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 로그인(인증)
    public MemberSignInResponseDTO authenticate(MemberSignInRequestDTO dto) {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("가입된 회원이 아닙니다."));

        String inputPassword = dto.getPassword();       // 사용자가 입력한 비밀번호
        String encodedPassword = member.getPassword();  // DB에 저장되어있는 비밀번호

        // 인코딩된 비밀번호를 디코딩하는 기능은 없고, 비교할 수 있는 메서드만 제공해줌
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // 토큰 발급
        String token = tokenProvider.createToken(member);

        return new MemberSignInResponseDTO(member, token);
    }

    // 회원정보 수정 메서드
    @Transactional
    public MemberSignInResponseDTO modify(MemberModifyRequestDTO dto, TokenUserInfo userInfo, String profilePath) {

        log.info("modifyDTO : {}, profilepath :{}", dto,profilePath);
        if (dto == null && profilePath == null) {
            throw new RuntimeException("수정된 회원정보가 없습니다.");
        }
        assert userInfo != null;
        Member member = memberRepository.findByEmail(userInfo.getEmail()).orElseThrow(() -> new RuntimeException("해당 ID에 대한 계정이 없습니다."));
        MemberModifyRequestDTO modifyRequestDTO = new MemberModifyRequestDTO(member);

        log.info("정보를 수정하는 회원 이름 : {}", member.getUserName());
        boolean flag = false;

        assert dto != null;
        if (dto.getPassword() != null) {
            modifyRequestDTO.setPassword(dto.getPassword());
            flag = true;
        }
        if (dto.getUserName() != null) {
            modifyRequestDTO.setUserName(dto.getUserName());
        }
        if (dto.getWeight() != null) {
            modifyRequestDTO.setWeight(dto.getWeight());
        }
        if (profilePath != null) {
            modifyRequestDTO.setProfilePath(profilePath);
        }

        log.info("수정된 회원 dto : {}", dto);

        Member saved = null;
        if (flag) {
            saved = memberRepository.save(modifyRequestDTO.toEntity(passwordEncoder));
        } else {
            saved = memberRepository.save(modifyRequestDTO.toEntity());
        }

        String token = tokenProvider.createToken(saved);

        log.info("회원정보 수정 성공!! saved user - {}", saved);

        return new MemberSignInResponseDTO(saved, token);
    }

    public String uploadProfileImage(MultipartFile originalFile) throws IOException {
        // 루트 디렉토리가 존재하는지 확인 후 존재하지 않으면 생성
        File rootDir = new File(rootPath);
        if (!rootDir.exists()) rootDir.mkdirs();

        // 파일의 이름을 중복될 수 없도록 변경
        String uniqueFileName = UUID.randomUUID() + "_" + originalFile.getOriginalFilename();

        // 파일을 서버에 저장
        File uploadFile = new File(rootPath + "/" + uniqueFileName);
        originalFile.transferTo(uploadFile);

        return uniqueFileName;
    }
}
