package com.clouddrive.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@TableName("user_table")
public class UserMode implements UserDetails {
    @TableId(value = "ID", type = IdType.AUTO)
    Integer id;
    @TableField("name")
    String name;
    @TableField("password")
    String pwd;
    String email;
    Boolean isEnable;
    Date createTime;
    Date updateTime;
    Date deleteTime;
    @TableField("auth")
    String authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) return new ArrayList<>();
        String[] authStr = authorities.split(";");
        List<GrantedAuthority> li = new ArrayList<>();
        for (String s : authStr) {
            li.add(new SimpleGrantedAuthority(s));
        }
        return li;
    }

    public void setAuthorities(List<? extends GrantedAuthority> authList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (GrantedAuthority g : authList) {
            stringBuilder.append(g.getAuthority());
            stringBuilder.append(';');
        }
        authorities = stringBuilder.toString();
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public String getUsername() {
        return name;
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
}
