package com.gig.collide.api.notice.service;

import com.gig.collide.api.notice.response.NoticeResponse;


public interface NoticeFacadeService {
    /**
     * 生成并发送短信验证码
     *
     * @param telephone
     * @return
     */
    public NoticeResponse generateAndSendSmsCaptcha(String telephone);
}
