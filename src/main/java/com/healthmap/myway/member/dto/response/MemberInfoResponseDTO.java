package com.healthmap.myway.member.dto.response;

import com.healthmap.myway.member.entity.Member;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponseDTO {

    private String email;
    private String userName;
    private LocalDate birthDate;
    private double weight;
    private String gender;
    private LocalDateTime joinDate;

    public MemberInfoResponseDTO(Member member) {
        this.email = member.getEmail();
        this.userName = member.getUserName();
        this.birthDate = member.getBirthDate();
        this.weight = member.getWeight();
        this.gender = String.valueOf(member.getGender());
        this.joinDate = member.getJoinDate();
    }
}
