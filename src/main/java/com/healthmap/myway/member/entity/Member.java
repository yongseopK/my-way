package com.healthmap.myway.member.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.healthmap.myway.member.entity.Member.Role.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
public class Member {

    // 회원 식별번호
    @Id
    @Column(name= "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    // 회원 이메일(이게 로그인할 때 사용됨)
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

    // 프로파일 사진 경로
    private String profileImg;

    public enum Role {
        ADMIN, COMMON
    }
}
