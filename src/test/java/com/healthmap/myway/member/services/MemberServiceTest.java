package com.healthmap.myway.member.services;

import com.healthmap.myway.member.dto.request.MemberSignUpRequsetDTO;
import com.healthmap.myway.member.dto.response.MemberSignUpResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("회원가입 테스트임")
    void registerTest() {
        //given
        MemberSignUpRequsetDTO dto = MemberSignUpRequsetDTO.builder()
                .email("kk0@kakao.com")
                .birthDate("2001-02-17")
                .gender("MALE")
                .password("alalal1234!")
                .userName("김용섭")
                .weight(80.0)
                .build();
        //when
        MemberSignUpResponseDTO responseDTO = memberService.create(dto);

        //then
        assertEquals("김용섭", responseDTO.getUserName());
    }


}