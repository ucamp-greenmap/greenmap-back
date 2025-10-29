package com.ucamp.greenmap.member.service.social;

import com.ucamp.greenmap.member.helper.constants.SocialLoginType;

public interface SocialOauth {
    String getOauthRedirectURL();
    String requestAccessToken(String code);

    default SocialLoginType type(){
        if(this instanceof GoogleOauth){
            return SocialLoginType.GOOGLE;
        }
        else{
            return null;
        }
    }
}
