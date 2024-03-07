package com.healthmap.myway.member.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignInRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
