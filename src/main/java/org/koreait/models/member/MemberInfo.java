package org.koreait.models.member;

import lombok.Builder;
import lombok.Data;
import org.koreait.entities.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Builder
public class MemberInfo implements UserDetails {

    private String email;
    private String password;

    private Member member;

    private  Collection<? extends  GrantedAuthority> authorities;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 권한 관리
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // account가 잠겨있지않느냐
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // 비밀번호가 만료되었는가
        return true;
    }

    @Override
    public boolean isEnabled() { // 계정이 활성화 되어있는가? -> 회원탈퇴할 때 (회원 정보만 파기)
        return true;
    }
}
