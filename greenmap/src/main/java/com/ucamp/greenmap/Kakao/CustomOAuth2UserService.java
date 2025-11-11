package com.ucamp.greenmap.Kakao;

import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            String providerId = oAuth2User.getAttribute("id").toString();
            Long kakaoId = Long.parseLong(providerId);

            // DB에서 사용자 조회
            Member member = memberRepository.findByKakaoId(kakaoId).orElse(null);

            // 사용자가 존재하는 경우에만 검증 (신규 회원가입은 OAuth2SuccessHandler에서 처리)
            if (member != null) {
                // 탈퇴한 계정인지 확인
                if (Boolean.FALSE.equals(member.getIsActive())) {
                    log.info("탈퇴한 계정 검거: kakaoId={}", kakaoId);
                    throw new OAuth2AuthenticationException(
                            new OAuth2Error("inactive_account", "탈퇴한 계정입니다. 로그인할 수 없습니다.", null));
                }
                // 활성 사용자면 그대로 반환
//                log.info("기존 카카오 사용자 로그인: kakaoId={}, memberId={}", kakaoId, member.getMemberId());
//                return new CustomOAuth2User(member, oAuth2User.getAttributes());
            }

            // 사용자가 없는 경우 (신규 회원가입) - OAuth2SuccessHandler에서 처리하도록 원본 OAuth2User 반환
            // CustomOAuth2User는 Member가 필수이므로, 원본 OAuth2User를 그대로 반환하여
            // OAuth2SuccessHandler에서 처리
            log.info("신규 카카오 사용자 감지: kakaoId={}, OAuth2SuccessHandler에서 회원가입 처리 예정", kakaoId);
            return oAuth2User; // 원본 OAuth2User 반환하여 OAuth2SuccessHandler에서 회원가입 처리

        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("OAuth2 사용자 로드 중 오류 발생", e);
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_error", "OAuth2 인증 중 오류가 발생했습니다.", null),
                    e);
        }
    }
}
