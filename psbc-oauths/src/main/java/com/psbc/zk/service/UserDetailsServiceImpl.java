package com.psbc.zk.service;



import com.psbc.zk.dao.UserDao;
import com.psbc.zk.entity.UserJwt;
import com.psbc.zk.entity.XcUserExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired(required = false)
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret,AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        XcUserExt userext = userDao.getUser(username);
        if(userext == null){
            //返回空给spring security表示用户不存在
            return null;
        }

//        userext.setPermissions(new ArrayList<XcMenu>());//权限暂时用静态的

        //取出正确密码（hash值）
        String password = userext.getPassword();
        //List<XcMenu> permissions = userext.getPermissions();
//        if(permissions == null){
//            permissions = new ArrayList<>();
//        }
//        List<String> user_permission = new ArrayList<>();
//        permissions.forEach(item-> user_permission.add(item.getCode()));
//        String user_permission_string  = StringUtils.join(user_permission.toArray(), ",");
        UserJwt userDetails = new UserJwt(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));
//        userDetails.setId(userext.getId());
//        userDetails.setUtype(userext.getUtype());//用户类型
//        userDetails.setCompanyId(userext.getCompanyId());//所属企业
        userDetails.setName(userext.getUsername());//用户名称
//        userDetails.setUserpic(userext.getUserpic());//用户头像
        return userDetails;
    }

}
