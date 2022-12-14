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
        //??????
        String username = loginRequest.getUsername();
        //??????
        String password = loginRequest.getPassword();

        //????????????
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);

        //??????????????????
        String access_token = authToken.getAccess_token();
        //??????????????????cookie
        this.saveCookie(access_token);
        HashMap<String, String> data = new HashMap<>();
        data.put("???????????????", access_token);
        return new ResponseModel(data);
    }

    //??????????????????cookie
    private void saveCookie(String token) {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);

    }

    //???cookie??????token
    private void clearCookie(String token) {

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);

    }

    //??????
    @PostMapping("/userlogout")
    public ResponseModel logout() {
        //??????cookie????????????????????????
        String uid = getTokenFormCookie();
        //??????redis??????token
        boolean result = authService.delToken(uid);
        //??????cookie
        this.clearCookie(uid);
        return new ResponseModel();
    }

    @GetMapping("/userjwt")
    public ResponseModel userjwt() {
        //??????cookie????????????????????????
        String uid = getTokenFormCookie();
        if (uid == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }

        //??????????????????redis?????????jwt??????
        AuthToken userToken = authService.getUserToken(uid);
        if (userToken != null) {
            //???jwt?????????????????????
            String jwt_token = userToken.getJwt_token();
            return new ResponseModel(jwt_token);
        }
        return null;
    }

    //??????cookie??????????????????
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
