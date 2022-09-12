package com.psbc.zk.controller;


import com.psbc.common.exception.ExceptionCast;
import com.psbc.common.response.ResponseModel;
import com.psbc.common.util.CookieUtil;
import com.psbc.zk.entity.AuthToken;
import com.psbc.zk.exception.AuthCode;
import com.psbc.zk.request.LoginRequest;
import com.psbc.zk.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangkun
 * @version 1.0
 **/
@RestController
@RequestMapping("/")
public class AuthController {

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Autowired
    AuthService authService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/valid")
    public boolean valid(String access_token, String clientId, String clientSecret) {
        boolean valid = authService.valid(access_token);
        return valid;
    }

    @PostMapping("/refresh")
    public AuthToken refresh(String id, String token, String clientId, String clientSecret) throws Exception {
        if (id == null || token == null || clientId == null || clientSecret == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_ERROR);
        }
        AuthToken authToken = authService.refresh(token, clientId, clientSecret);
        return authToken;
    }

    @PostMapping("/userlogin")
    public ResponseModel login(LoginRequest loginRequest) {
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())) {
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        //账号
        String username = loginRequest.getUsername();
        //密码
        String password = loginRequest.getPassword();

        //申请令牌
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);

        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //将令牌存储到cookie
        this.saveCookie(access_token);
        HashMap<String, String> data = new HashMap<>();
        data.put("身份令牌：", access_token);
        return new ResponseModel(data);
    }

    //将令牌存储到cookie
    private void saveCookie(String token) {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);

    }

    //从cookie删除token
    private void clearCookie(String token) {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);

    }

    //退出
    @PostMapping("/userlogout")
    public ResponseModel logout() {
        //取出cookie中的用户身份令牌
        String uid = getTokenFormCookie();
        //删除redis中的token
        boolean result = authService.delToken(uid);
        //清除cookie
        this.clearCookie(uid);
        return new ResponseModel();
    }

    @GetMapping("/userjwt")
    public ResponseModel userjwt() {
        //取出cookie中的用户身份令牌
        String uid = getTokenFormCookie();
        if (uid == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        //拿身份令牌从redis中查询jwt令牌
        AuthToken userToken = authService.getUserToken(uid);
        if (userToken != null) {
            //将jwt令牌返回给用户
            String jwt_token = userToken.getJwt_token();
            return new ResponseModel(jwt_token);
        }
        return null;
    }

    //取出cookie中的身份令牌
    private String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map != null && map.get("uid") != null) {
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }
}
