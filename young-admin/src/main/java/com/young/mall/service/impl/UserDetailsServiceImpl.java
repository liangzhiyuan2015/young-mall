package com.young.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.young.db.entity.YoungAdmin;
import com.young.mall.domain.AdminUser;
import com.young.mall.exception.Asserts;
import com.young.mall.service.AdminCacheService;
import com.young.mall.service.PermissionService;
import com.young.mall.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * @Description: spring-security UserDetailsService实现类
 * @Author: yqz
 * @CreateDate: 2020/10/25 17:07
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdminService adminService;

    @Autowired
    private PermissionService permissionService;

    @Resource
    private AdminCacheService adminCacheService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        YoungAdmin youngAdmin = getYoungAdmin(username);

        AdminUser adminUser = new AdminUser();
        BeanUtil.copyProperties(youngAdmin, adminUser);
        Set<GrantedAuthority> authoritySet = getAuthorities(adminUser);
        adminUser.setAuthorities(authoritySet);

        return adminUser;
    }

    /**
     * 获取用户信息
     *
     * @param username
     * @return
     */
    public YoungAdmin getYoungAdmin(String username) {

        YoungAdmin youngAdmin = null;

        Object admin = adminCacheService.getAdmin(username);
        //强转类型之前做判断，避免redis出现异常，导致强转失败，影响程序正常运行
        if (admin instanceof YoungAdmin) {
            youngAdmin = ((YoungAdmin) admin);
            logger.error("缓存中获取用户信息：{}", JSONUtil.toJsonStr(admin));
            return youngAdmin;
        }

        Optional<YoungAdmin> adminOptional = adminService.findAdminByName(username);
        if (!adminOptional.isPresent()) {
            Asserts.fail("无该用户");
        }
        youngAdmin = adminOptional.get();
        logger.error("数据库中获取用户信息：{}", JSONUtil.toJsonStr(youngAdmin));

        adminCacheService.setAdmin(youngAdmin);
        return youngAdmin;
    }

    /**
     * 查询 authoritySet
     *
     * @param adminUser
     * @return
     */
    public Set<GrantedAuthority> getAuthorities(AdminUser adminUser) {

        Object permissionsList = adminCacheService.getPermissionsList(adminUser.getUsername());

        //强转类型之前做判断，避免redis出现异常，导致强转失败，影响程序正常运行
        if (permissionsList instanceof Set) {
            return unmodifiableSet((Set<String>) permissionsList);
        }

        Optional<Set<String>> optionalSet = permissionService.queryByRoleIds(adminUser.getRoleIds());
        Set<String> permissions = optionalSet.get();
        if (!optionalSet.isPresent()) {
            Asserts.fail("查询权限失败");
        }

        //把权限id存入缓存，避免每次查询数据库缓慢
        adminCacheService.setResourceList(adminUser.getUsername(),permissions);

        Set<GrantedAuthority> authoritySet = unmodifiableSet(permissions);

        return authoritySet;
    }

    public Set<GrantedAuthority> unmodifiableSet(Set<String> permissions) {
        Collection<? extends GrantedAuthority> authorities
                = AuthorityUtils.createAuthorityList(permissions.toArray(new String[0]));
        Set<GrantedAuthority> authoritySet = Collections.unmodifiableSet(sortAuthorities(authorities));
        return authoritySet;
    }


    private static SortedSet<GrantedAuthority> sortAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per
        // UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority,
                    "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>,
            Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to
            // the set.
            // If the authority is null, it is a custom authority and should precede
            // others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }
}
