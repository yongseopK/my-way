package com.healthmap.myway.member.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthmap.myway.member.entity.Member;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NotBlank
@AllArgsConstructor
@Builder
public class MemberSignUpResponseDTO {

    private String email;
    private String userName;

    @JsonProperty("join-date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDate;

    // 엔터티를 DTO로 변환하는 생성자
    public MemberSignUpResponseDTO(Member member) {
        this.email = member.getEmail();
        this.userName = member.getUserName();
        this.joinDate = member.getJoinDate();
    }
}
