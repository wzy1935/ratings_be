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

    public record LoginRT(String code, String jwt) {}
    public LoginRT login(String name, String password) {
        if (name.length() > 20 || password.length() > 20) return new LoginRT("INVALID", null);

        String encodedPassword = authUtils.encode(password);
        UserEntity user = userMapper.getUserByName(name);
        if (!Objects.equals(user.password(), encodedPassword)) return new LoginRT("INCORRECT", null);

        String jwt = authUtils.createJwt(name);
        return new LoginRT("SUCCESS", jwt);
    }

    public String signup(String name, String password) {
        if (name.length() > 20 || password.length() > 20) return "INVALID";

        UserEntity user = userMapper.getUserByName(name);
        if (user != null) return "ALREADY_EXISTED";

        String encodedPassword = authUtils.encode(password);
        user = new UserEntity(null, name, encodedPassword, false);
        userMapper.addUser(user);
        return "SUCCESS";
    }

    public String changePassword(String name, String oldPassword, String newPassword) {
        if (newPassword.length() > 20) return "INVALID";
        UserEntity user = userMapper.getUserByName(name);
        if (!authUtils.encode(oldPassword).equals(user.password())) return "WRONG_OLD_PASSWORD";

        UserEntity newUser = new UserEntity(user.id(), user.name(), authUtils.encode(newPassword), user.is_admin());
        userMapper.updateUserById(newUser);
        return "SUCCESS";
    }
}
