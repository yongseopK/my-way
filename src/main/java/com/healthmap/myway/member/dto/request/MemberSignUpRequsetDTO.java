package com.healthmap.myway.member.dto.request;


import com.healthmap.myway.member.entity.Member;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.LocalDate;

import static com.healthmap.myway.member.entity.Member.*;


// 회원가입 요청을 처리하는 DTO
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpRequsetDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @Size(min = 2, max = 5)
    private String userName;

    @NotBlank
    private String gender;

    @NotBlank
    private String birthDate;

    //@NotBlank
    private double weight;


    // 요청값을 User Entity로 변환하는 코드
    public Member toEntity(PasswordEncoder passwordEncoder, String profilePath) {
        return Member.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .userName(this.userName)
                .profileImg(profilePath)
                .weight(this.weight)
                .gender(Gender.fromString(this.gender))
                .birthDate(LocalDate.parse(birthDate))
                .build();
    }
}
