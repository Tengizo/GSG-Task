package com.gsg.task.gsgtask.scheduler;

import com.gsg.task.gsgtask.external.YTService;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.socket.notification.YTSocketService;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;

@Slf4j
public class UserUpdateTask implements Runnable {
    private User user;
    private final UserRepository userRepository;
    private final YTService ytService;
    private final HttpClient client;
    private final YTSocketService ytSocketService;

    public UserUpdateTask(User user, UserRepository userRepository, YTService ytService, YTSocketService ytSocketService) {
        this.user = user;
        this.userRepository = userRepository;
        this.ytService = ytService;
        this.ytSocketService = ytSocketService;
        client = HttpClient.newHttpClient();

    }

    @Override
    public void run() {
        log.info("Job Run for user: " + user.getUsername());
        String videoId = this.ytService.getTrendingVideo(user.getCountry());
        String commentId = null;
        if (videoId != null)
            commentId = this.ytService.getComment(videoId);
        String commentLink = ytService.toCommentLink(videoId, commentId);
        String videoLink = ytService.toLink(videoId);
        if (commentId == null) {
            log.error("Error during fetching video and comment ");
            return;
        }
        if (!videoLink.equals(user.getYtVideoLink()) || !commentLink.equals(user.getCommentLink())) {
            log.info("Updating user: " + user.getUsername());
            log.info("       links: " + ytService.toCommentLink(videoId, commentId));
            log.info("       links: " + ytService.toLink(videoId));
            user.setYtVideoLink(videoLink);
            user.setCommentLink(commentLink);
            this.userRepository.updateUser(user);
            this.ytSocketService.sendYTLinkUpdate(user);
        }
    }
}
