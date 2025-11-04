package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.member.JwtTokenProviderBasic;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.request.SignUpRequest;
import com.ucamp.greenmap.member.dto.response.SignUpResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProviderBasic jwtTokenProvider;
    private final ImageRepository imageRepository;

    @Override
    public SignUpResponse signup(SignUpRequest request) {

        //  이메일 형식 검증
        if (!request.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }

    //  이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }


        //  닉네임 중복 체크
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        //  비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        //  기본 이미지 설정
        String defaultImage = "https://em-content.zobj.net/thumbs/120/apple/325/leaf-fluttering-in-wind_1f343.png";

        String imageUrl = (request.getImageUrl() != null) ? request.getImageUrl() : defaultImage;

        Image image = Image.builder()
                .imageUrl(imageUrl)
                .build();

        imageRepository.save(image);


        //  회원 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .image(image)
                .build();

        memberRepository.save(member);

        //  JWT 발급
        String token = jwtTokenProvider.createToken(member.getEmail());

        //  DTO 반환
        return SignUpResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageUrl(member.getImage().getImageUrl())
                .accessToken(token)
                .build();
    }
}

