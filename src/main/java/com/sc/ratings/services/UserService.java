package com.sc.ratings.services;

import com.sc.ratings.entities.UserEntity;
import com.sc.ratings.mappers.UserMapper;
import com.sc.ratings.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    AuthUtils authUtils;

    static boolean checkNames(String name) {
        return name.length() < 20 && name.strip().length() > 0;
    }

    public record LoginRT(String code, String jwt) {}
    public LoginRT login(String name, String password) {
        if (!(checkNames(name) && checkNames(password))) return new LoginRT("INVALID", null);

        String encodedPassword = authUtils.encode(password);
        UserEntity user = userMapper.getUserByName(name);
        if (user == null || !Objects.equals(user.password(), encodedPassword)) {
            return new LoginRT("INCORRECT", null);
        }

        String jwt = authUtils.createJwt(name);
        return new LoginRT("SUCCESS", jwt);
    }

    public String signup(String name, String password) {
        if (!(checkNames(name) && checkNames(password))) return "INVALID";

        UserEntity user = userMapper.getUserByName(name);
        if (user != null) return "ALREADY_EXISTED";

        String encodedPassword = authUtils.encode(password);
        user = new UserEntity(null, name, encodedPassword, false);
        userMapper.addUser(user);
        return "SUCCESS";
    }

    public String changePassword(String name, String oldPassword, String newPassword) {
//        if (checkNames(newPassword)) return "INVALID";
        UserEntity user = userMapper.getUserByName(name);
        if (!authUtils.encode(oldPassword).equals(user.password())) return "WRONG_OLD_PASSWORD";

        UserEntity newUser = new UserEntity(user.id(), user.name(), authUtils.encode(newPassword), user.is_admin());
        userMapper.updateUserById(newUser);
        return "SUCCESS";
    }

    public String checkRoleByName(String userName) {
        if (userName == null) return "BASE";

        UserEntity user = userMapper.getUserByName(userName);
        boolean isAdmin = user.is_admin();
        return isAdmin ? "ADMIN" : "USER";
    }
}
