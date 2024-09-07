package com.example.demo.helpers;

import com.example.demo.exceptions.AuthorizationException;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.models.Creatable;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class RestrictHelper {
    public static final String ADMIN_ERROR_MESSAGE = "You are not admin";
    public static final String ADMIN_MODERATOR_ERROR_MESSAGE = "You must be admin or moderator";
    private static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";

    private final UserService userService;
    private final PasswordEncoder encoder;

    @Autowired
    public RestrictHelper(UserService userService, PasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

//    public boolean isUserAdmin(User user){
//        if (!user.getRole().getRoleName().equals(Role.RoleType.ADMIN)){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
//                    ADMIN_ERROR_MESSAGE);
//        }
//        return true;
//    }
    public void isUserAdminOrEmployee(User user){
        if (!user.getRole().getRoleName().equals(Role.RoleType.ADMIN) && !user.getRole().getRoleName().equals(Role.RoleType.EMPLOYEE)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ADMIN_MODERATOR_ERROR_MESSAGE);
        }
    }
//    public <T extends Creatable> void isUserACreator(T entity, User user) {
//        if (user.getId() != entity.getCreator().getId()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized");
//        }
//    }
//    public <T extends Creatable> void deletePermission(User user) {
//        if ((!user.getRole().getRoleName().equals(Role.RoleType.ADMIN) && !user.getRole().getRoleName().equals(Role.RoleType.EMPLOYEE))){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized");
//        }
//    }
//    public <T extends Creatable> void deletePermission(T entity, User user) {
//        if (user.getId() != entity.getCreator().getId() && (!user.getRole().getRoleName().equals(Role.RoleType.ADMIN) && !user.getRole().getRoleName().equals(Role.RoleType.EMPLOYEE))){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized");
//        }
//    }

    public User verifyAuthentication(String username, String password) {
        try {
            Optional<User> optionalUser = userService.getUserByUsername(username);
            if (!optionalUser.isPresent()){
                throw new EntityNotFoundException("User", "username", username);
            }
            User user = optionalUser.get();
            if (!encoder.matches(password, user.getPassword())) {
                throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthorizationException(INVALID_AUTHENTICATION_ERROR);
        }
    }
}
