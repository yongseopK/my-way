package com.healthmap.myway.member.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.healthmap.myway.member.entity.Member.Role.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "email")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
@ToString
public class Member {

    //// 회원 식별번호
    //@Id
    //@GeneratedValue(generator = "system-uuid")
    //@GenericGenerator(name = "system-uuid", strategy = "uuid")
    //private String id;

    // 회원 이메일(이게 로그인할 때 사용됨)
    //@Column(unique = true, nullable = false)
    @Id
    @Column(unique = true, nullable = false)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 유저 이름
    @Column(nullable = false)
    private String userName;

    // 가입 날짜
    @CreationTimestamp
    private LocalDateTime joinDate;

    // 권한
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = COMMON;

    // 성별
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 프로필 사진 경로
    private String profileImg;

    // 회원의 몸무게
    private double weight;

    // 생년월일
    private LocalDate birthDate;

    public enum Role {
        ADMIN, COMMON
    }

    public enum Gender {
        MALE, FEMALE;

        public static Gender fromString(String gender) {
            if (gender != null) {
                switch (gender.toLowerCase()) {
                    case "male":
                        return MALE;
                    case "female":
                        return FEMALE;
                    default:
                        throw new IllegalArgumentException("유효하지 않은 값입니다.");
                }
            }
            throw new IllegalArgumentException("Gender cannot be null");
        }
    }
}
