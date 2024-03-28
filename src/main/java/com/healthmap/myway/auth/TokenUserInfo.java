package com.healthmap.myway.auth;

import com.healthmap.myway.member.entity.Member.Role;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TokenUserInfo {

    //private String id;
    private String email;
    private Role role;
}
