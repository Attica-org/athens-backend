package com.attica.athens.global.auth.domain;

import com.attica.athens.domain.member.domain.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Long id;
    private final String password;
    private final String role;
    private Map<String, Object> attributes;

    public Long getUserId() {
        return id;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add((GrantedAuthority) () -> role);

        return collection;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails create(Member member, Map<String, Object> attributes) {
        CustomUserDetails customUserDetails = new CustomUserDetails(member.getId(), member.getPassword(),
                member.getRole().name());
        customUserDetails.setAttributes(attributes);
        return customUserDetails;
    }

    private void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
