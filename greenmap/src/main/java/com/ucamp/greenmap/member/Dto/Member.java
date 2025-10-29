package com.ucamp.greenmap.member.Dto;

import com.ucamp.greenmap.image.domain.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Member {

    private Long memberId;
    private Image image;
    private String email;
    private String nickname;
    private String password;

}
