//package com.ucamp.greenmap.Kakao;
//
//import com.ucamp.greenmap.member.domain.Member;
//import com.ucamp.greenmap.member.repository.MemberRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.OAuth2Error;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final MemberRepository memberRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String providerId = oAuth2User.getAttribute("id").toString();
//
//        // DB에서 사용자 조회
//        Member member = memberRepository.findByKakaoId(Long.parseLong(providerId))
//                .orElseThrow();
//
//        // ✅ 핵심 검증 로직
//        if (!member.getIsActive()) {
//            log.info("탈퇴한 계정 검거");
//            throw new OAuth2AuthenticationException(new OAuth2Error("inactive_account", "탈퇴한 계정입니다. 로그인할 수 없습니다.", null));
//        }
//
//        return new CustomOAuth2User(member, oAuth2User.getAttributes());
//    }
//}
