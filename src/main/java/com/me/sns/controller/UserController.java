package com.me.sns.controller;

import com.me.sns.controller.request.UserJoinRequest;
import com.me.sns.controller.request.UserLoginRequest;
import com.me.sns.controller.response.AlarmResponse;
import com.me.sns.controller.response.Response;
import com.me.sns.controller.response.UserJoinResponse;
import com.me.sns.controller.response.UserLoginResponse;
import com.me.sns.model.User;
import com.me.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        User user = userService.join(request.getName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        /**
         * TODO: 알람 조회시 이미 알고 있는 User를 다시 조회하기 때문에 DB에서 2번의 IO가 발생함. 이를 1번만 IO가 발생하도록 변경함.
         */
        User user = (User) authentication.getPrincipal();
        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
