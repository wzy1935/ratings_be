package com.sc.ratings.utils.authaop;

import com.sc.ratings.entities.UserEntity;
import com.sc.ratings.exceptions.NotAdminException;
import com.sc.ratings.exceptions.NotUserException;
import com.sc.ratings.mappers.UserMapper;
import com.sc.ratings.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AopAuth {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    AuthUtils authUtils;
    @Autowired
    UserMapper userMapper;


    @Pointcut("@annotation(com.sc.ratings.utils.authaop.Auth)")
    public void pointcut() {}

    @Before("pointcut() && @annotation(auth)")
    public void before(JoinPoint joinPoint, Auth auth) throws Exception {
        String jwt = request.getHeader("Authorization");
        String userName = authUtils.verifyJwt(jwt);

        if (auth.type() == Auth.Type.USER) {
            if (userName == null) throw new NotUserException();
        } else if (auth.type() == Auth.Type.ADMIN) {
            if (userName == null) throw new NotUserException();
            UserEntity user = userMapper.getUserByName(userName);
            if (!user.is_admin()) throw new NotAdminException();
        }

    }
}
