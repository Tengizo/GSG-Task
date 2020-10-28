package com.gsg.task.gsgtask.socket.notification;

import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.socket.notification.model.YTData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class YTSocketService {


    private final SimpMessagingTemplate template;

    public YTSocketService(SimpMessagingTemplate template) {

        this.template = template;
    }

    @Primary
    @Async
    public void sendYTLinkUpdate(User user) {
        YTData ytData = YTData.builder()
                .commentLink(user.getCommentLink())
                .videoLink(user.getYtVideoLink())
                .build();
        template.convertAndSend("/yt/"+user.getId(), ytData);

    }
}
