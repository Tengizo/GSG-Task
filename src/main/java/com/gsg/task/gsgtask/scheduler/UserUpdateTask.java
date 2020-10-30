package com.gsg.task.gsgtask.scheduler;

import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.external.YTService;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.socket.notification.YTSocketService;
import lombok.extern.slf4j.Slf4j;

/**
 * It's a task for one user, responsible for getting youtube links and updating users if needed
 */
@Slf4j
public class UserUpdateTask implements Runnable {
    private final User user;
    private final UserRepository userRepository;
    private final YTService ytService;
    private final YTSocketService ytSocketService;

    public UserUpdateTask(User user, UserRepository userRepository, YTService ytService, YTSocketService ytSocketService) {
        this.user = user;
        this.userRepository = userRepository;
        this.ytService = ytService;
        this.ytSocketService = ytSocketService;

    }

    @Override
    public void run() {
        log.info("Job Run for user: " + user.getUsername());
        try {
            runTask();
        } catch (AppException appException) {
            log.error("User: "+this.user.getUsername()+" Problem with youtube service: ", appException);
        }
    }
    /**
     * calls youtube service and updates user if needed
     */
    void runTask() {
        String commentLink = null;
        String videoLink = null;
        String videoId = this.ytService.getTrendingVideo(user.getCountry());
        if (videoId != null) {
            videoLink = ytService.toVideoLink(videoId);
            String commentId = this.ytService.getComment(videoId);
            if (commentId != null) {
                commentLink = ytService.toCommentLink(videoId, commentId);
            } else {
                log.info("Youtube returned 0 comments");
            }
        } else {
            log.info("Youtube returned empty response for region: " + user.getCountry());
        }
        if (isLinksChanged(videoLink, commentLink)) {
            log.info("Updating user: " + user.getUsername());
            log.info("       links: " + videoLink);
            log.info("       links: " + commentLink);
            user.setYtVideoLink(videoLink);
            user.setCommentLink(commentLink);
            this.userRepository.updateUser(user);
            this.ytSocketService.sendYTLinkUpdate(user);
        }
    }

    private boolean isLinksChanged(String videoLink, String commentLink) {
        if (videoLink == null) return user.getYtVideoLink() != null;
        if (!videoLink.equals(user.getYtVideoLink())) return true;
        if (commentLink == null) return user.getCommentLink() != null;
        return !commentLink.equals(user.getCommentLink());
    }

    public User getTaskUser() {
        return this.user;
    }
}
