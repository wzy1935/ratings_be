package com.sc.ratings.controllers;

import com.sc.ratings.mappers.UserMapper;
import com.sc.ratings.services.UserService;
import com.sc.ratings.utils.AuthUtils;
import com.sc.ratings.utils.DataMap;
import com.sc.ratings.utils.RespData;
import com.sc.ratings.utils.authaop.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    AuthUtils authUtils;
    @Autowired
    UserMapper userMapper;

    record UserPT(String name, String password) {}
    record ChangePasswordPT(String old_password, String new_password) {}

    @PostMapping("api/user/login")
    public RespData login(@RequestBody UserPT user) {
        var loginRT = userService.login(user.name(), user.password());
        if (loginRT.code().equals("SUCCESS")) {
            var returnData = DataMap.builder()
                    .set("jwt", loginRT.jwt());
            return RespData.resp(loginRT.code(), returnData);
        }
        return RespData.resp(loginRT.code());
    }

    @PostMapping("api/user/signup")
    public RespData signup(@RequestBody UserPT user) {
        String code = userService.signup(user.name(), user.password());
        return RespData.resp(code);
    }

    @GetMapping("api/user/user-info")
    public RespData userInfo() {
        String userName = authUtils.getCurrentUserName();

        return RespData.success(DataMap.builder()
                .set("role", userService.checkRoleByName(authUtils.getCurrentUserName()))
                .set("user_name", userName)
                .set("user_id", userName == null ? null : userMapper.getUserByName(userName).id())
        );
    }

    @Auth(type=Auth.Type.USER)
    @PostMapping("api/user/change-password")
    public RespData changePassword(@RequestBody ChangePasswordPT pt) {
        String userName = authUtils.getCurrentUserName();
        String code = userService.changePassword(userName, pt.old_password, pt.new_password);
        return RespData.resp(code);
    }
}
