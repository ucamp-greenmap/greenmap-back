package com.ucamp.greenmap.member.dto.response;

import com.ucamp.greenmap.image.dto.response.ImageResponse;
import com.ucamp.greenmap.member.domain.Member;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MemberResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private Boolean isActive;
    private ImageResponse image;
    private String badgeUrl;

    public static MemberResponse memberResponse(Member member) {

        ImageResponse imageResponse = null;
        if (member.getImage() != null) {
            imageResponse = ImageResponse.builder()
                    .imageId(member.getImage().getImageId())
                    .imageUrl(member.getImage().getImageUrl())
                    .build();
        }

        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .isActive(member.getIsActive())
                .image(imageResponse)
                .build();
    }
}
