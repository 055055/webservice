package com.toyproject.webservice.config.auth;

import com.toyproject.webservice.config.auth.dto.OAuthAttributes;
import com.toyproject.webservice.config.auth.dto.SessionUser;
import com.toyproject.webservice.domain.user.User;
import com.toyproject.webservice.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //registrationId: 현재 로그인 진행 중인 서비스를 구분하는 코드 (구글인지 네이버 인지)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                                        .getUserInfoEndpoint().getUserNameAttributeName();
        //userNameAttributeName OAuth2로그인 진행시 키가 되는 필드 값. PK(Primary Key) 네이버 로그인과 구글 로그인 동시 지원시 사용
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        //OAuthAttributes OAuth2UserService를통해 가져온 OAuth2User의 attributes를 담을 클래

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user",new SessionUser(user));
        //SessionUser 세션에 사용자 정보를 저장하기 위한 dto 클래스
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());

    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                                    .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                                    .orElse(attributes.toEntity());
        return userRepository.save(user);
    }
}
