package com.healthmap.myway.auth;

import com.healthmap.myway.member.entity.Member;
import com.healthmap.myway.member.entity.Member.Role;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TokenUserInfo {

    private String userId;
    private String email;
    private Role role;
}
