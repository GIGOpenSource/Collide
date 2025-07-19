package com.gig.collide.users.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.user.request.UserModifyRequest;
import com.gig.collide.api.user.response.data.BasicUserInfo;
import com.gig.collide.api.user.response.data.UserInfo;
import com.gig.collide.file.FileService;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.users.domain.entity.convertor.UserConvertor;
import com.gig.collide.users.domain.service.UserService;
import com.gig.collide.users.infrastructure.exception.UserException;
import com.gig.collide.users.param.UserModifyParam;
import com.gig.collide.web.vo.Result;
import cn.hutool.crypto.digest.DigestUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static com.gig.collide.users.infrastructure.exception.UserErrorCode.*;

/**
 * 用户信息
 *
 * @author
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;


    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo() {
        String userId = (String) StpUtil.getLoginId();
        User user = userService.findById(Long.valueOf(userId));

        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        return Result.success(UserConvertor.INSTANCE.mapToVo(user));
    }

    @GetMapping("/queryUserByTel")
    public Result<BasicUserInfo> queryUserByTel(String telephone) {
        User user = userService.findByTelephone(telephone);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        return Result.success(UserConvertor.INSTANCE.mapToBasicVo(user));
    }

    @PostMapping("/modifyNickName")
    public Result<Boolean> modifyNickName(@Valid @RequestBody UserModifyParam userModifyParam) {
        String userId = (String) StpUtil.getLoginId();

        //修改信息
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(Long.valueOf(userId));
        userModifyRequest.setNickName(userModifyParam.getNickName());

        Boolean registerResult = userService.modify(userModifyRequest).getSuccess();
        return Result.success(registerResult);
    }

    @PostMapping("/modifyPassword")
    public Result<Boolean> modifyPassword(@Valid @RequestBody UserModifyParam userModifyParam) {
        //查询用户信息
        String userId = (String) StpUtil.getLoginId();
        User user = userService.findById(Long.valueOf(userId));

        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        if (!StringUtils.equals(user.getPasswordHash(), DigestUtil.md5Hex(userModifyParam.getOldPassword()))) {
            throw new UserException(USER_PASSWD_CHECK_FAIL);
        }
        //修改信息
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(Long.valueOf(userId));
        userModifyRequest.setPassword(userModifyParam.getNewPassword());
        Boolean registerResult = userService.modify(userModifyRequest).getSuccess();
        return Result.success(registerResult);
    }

    @PostMapping("/modifyProfilePhoto")
    public Result<String> modifyProfilePhoto(@RequestParam("file_data") MultipartFile file) throws Exception {
        String userId = (String) StpUtil.getLoginId();
        String prefix = "https://nfturbo-file.oss-cn-hangzhou.aliyuncs.com/";

        if (null == file) {
            throw new UserException(USER_UPLOAD_PICTURE_FAIL);
        }
        String filename = file.getOriginalFilename();
        InputStream fileStream = file.getInputStream();
        String path = "profile/" + userId + "/" + filename;
        var res = fileService.upload(path, fileStream);
        if (!res) {
            throw new UserException(USER_UPLOAD_PICTURE_FAIL);
        }
        //修改信息
        UserModifyRequest userModifyRequest = new UserModifyRequest();
        userModifyRequest.setUserId(Long.valueOf(userId));
        userModifyRequest.setProfilePhotoUrl(prefix + path);
        Boolean registerResult = userService.modify(userModifyRequest).getSuccess();
        if (!registerResult) {
            throw new UserException(USER_UPLOAD_PICTURE_FAIL);
        }
        return Result.success(prefix + path);
    }

    private void refreshUserInSession(String userId) {
        User user = userService.getById(userId);
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
    }
}
