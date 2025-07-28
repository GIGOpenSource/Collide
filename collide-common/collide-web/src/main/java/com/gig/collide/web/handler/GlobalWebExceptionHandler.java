package com.gig.collide.web.handler;

import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.SystemException;
import com.gig.collide.web.vo.Result;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

import static com.gig.collide.base.response.ResponseCode.SYSTEM_ERROR;
/**
 * @author GIGTeam
 */
@ControllerAdvice
@Slf4j
public class GlobalWebExceptionHandler {

    /**
     * 自定义方法参数校验异常处理器
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException occurred.", ex);
        Map<String, String> errors = Maps.newHashMapWithExpectedSize(1);
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * 文件上传大小超限异常处理器
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("文件上传大小超限异常", ex);
        Result result = new Result();
        result.setCode("UPLOAD_SIZE_EXCEEDED");
        result.setResponseMessage("上传文件过大，单个文件最大支持100MB，请压缩后重试");
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义业务异常处理器
     *
     * @param bizException
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result exceptionHandler(BizException bizException) {
        log.error("bizException occurred.", bizException);
        Result result = new Result();
        result.setCode(bizException.getErrorCode().getCode());
        if (bizException.getMessage() == null) {
            result.setResponseMessage(bizException.getErrorCode().getMessage());
        } else {
            result.setResponseMessage(bizException.getMessage());
        }
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义系统异常处理器
     *
     * @param systemException
     * @return
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result systemExceptionHandler(SystemException systemException) {
        log.error("systemException occurred.", systemException);
        Result result = new Result();
        result.setCode(systemException.getErrorCode().getCode());
        if (systemException.getMessage() == null) {
            result.setResponseMessage(systemException.getErrorCode().getMessage());
        } else {
            result.setResponseMessage(systemException.getMessage());
        }
        result.setSuccess(false);
        return result;
    }

    /**
     * 自定义系统异常处理器
     *
     * @param throwable
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result throwableHandler(Throwable throwable) {
        log.error("throwable occurred.",throwable);
        Result result = new Result();
        result.setCode(SYSTEM_ERROR.name());
        result.setResponseMessage("哎呀，当前网络比较拥挤，请您稍后再试~");
        result.setSuccess(false);
        return result;
    }
}
