package com.healthmap.myway.member.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDeleteRequestDTO {

    @NotBlank
    private String password;
}
