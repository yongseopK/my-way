package com.healthmap.myway.member.dto.response;

import com.healthmap.myway.member.entity.Member;
import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberModifyResponseDTO {

    private String email;
    private String userName;
    private String profileImage;
    private String role;
    private String token;


    public MemberModifyResponseDTO(Member member, String token) {
        this.email = member.getEmail();
        this.userName = member.getUserName();
        this.profileImage = member.getProfileImg();
        this.role = member.getRole().toString();
        this.token = token;
    }

}
