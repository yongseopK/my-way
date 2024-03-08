package com.healthmap.myway.member.dto.request;

import com.healthmap.myway.member.entity.Member;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberModifyRequestDTO {

    @NotBlank
    private String email;

    private LocalDate birthDate;

    private String password;

    private String profilePath;

    private String userName;

    private Double weight;

    private String gender;

    private LocalDateTime joinDate;

    public MemberModifyRequestDTO(Member member) {
        this.email = member.getEmail();
        this.userName = member.getUserName();
        this.password = member.getPassword();
        this.profilePath = member.getProfileImg();
        this.weight = member.getWeight();
        this.birthDate = member.getBirthDate();
        this.gender = member.getGender().toString();
        this.joinDate = member.getJoinDate();
    }

    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(this.email)
                .birthDate(this.birthDate)
                .password(passwordEncoder.encode(this.password))
                .profileImg(this.profilePath)
                .weight(this.weight)
                .userName(this.userName)
                .gender(Member.Gender.valueOf(this.gender))
                .joinDate(this.joinDate)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .email(this.email)
                .birthDate(this.birthDate)
                .password(this.password)
                .profileImg(this.profilePath)
                .weight(this.weight)
                .userName(this.userName)
                .gender(Member.Gender.valueOf(this.gender))
                .joinDate(this.joinDate)
                .build();
    }
}
