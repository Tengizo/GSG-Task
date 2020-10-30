package com.gsg.task.gsgtask.scheduler;

import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.external.YTService;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.socket.notification.YTSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;

import static com.gsg.task.gsgtask.utils.TestUtils.getTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserUpdateTaskTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private YTService ytService;
    @Mock
    private YTSocketService ytSocketService;
    private final User user = getTestUser();
    private UserUpdateTask userUpdateTask;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void beforeEach() {
        this.userUpdateTask = new UserUpdateTask(user, userRepository, ytService, ytSocketService);
    }

    @Test
    void when_problemWithYtConnection() {
        AppException exceptionToThrow = new AppException(ExceptionType.YOUTUBE_SERVICE_PROBLEM, "test_error");
        when(this.ytService.getTrendingVideo(any(String.class))).thenThrow(exceptionToThrow);
        AppException exception = assertThrows(AppException.class, () -> this.userUpdateTask.runTask());
        assertEquals(exception, exceptionToThrow);
        verifyYtServiceCalls(Mockito.never());
    }

    @Test
    void when_whenVideoNotFound() {
        when(this.ytService.getTrendingVideo(any(String.class))).thenReturn(null);
        this.userUpdateTask.runTask();
        verifyYtServiceCalls(Mockito.never());
    }

    @Test
    void when_whenCommentNotFound() {
        when(this.ytService.getTrendingVideo(any(String.class))).thenReturn(null);
        this.userUpdateTask.runTask();
        verify(this.ytService, Mockito.never()).toCommentLink(any(String.class), any(String.class));
    }

    @Test
    void when_videoAndCommentAreNotChanged() {
        String trendingVideoId = "testVideoID";
        String commentId = "testCommentID";
        when(this.ytService.getTrendingVideo(any(String.class))).thenReturn(trendingVideoId);
        when(this.ytService.getComment(trendingVideoId)).thenReturn(commentId);
        when(this.ytService.toCommentLink(trendingVideoId, commentId)).thenReturn(this.user.getCommentLink());
        when(this.ytService.toVideoLink(trendingVideoId)).thenReturn(this.user.getYtVideoLink());
        this.userUpdateTask.runTask();
        verifyInternalServiceCalls(Mockito.never());
    }

    @Test
    void when_videoAndCommentAreChanged() {
        String trendingVideoId = "testVideoID";
        String commentId = "testCommentID";
        String commentLink = trendingVideoId + commentId + "link";
        String videoLink = trendingVideoId + "link";
        when(this.ytService.getTrendingVideo(any(String.class))).thenReturn(trendingVideoId);
        when(this.ytService.getComment(trendingVideoId)).thenReturn(commentId);
        when(this.ytService.toCommentLink(trendingVideoId, commentId)).thenReturn(commentLink);
        when(this.ytService.toVideoLink(trendingVideoId)).thenReturn(videoLink);
        this.userUpdateTask.runTask();
        verify(this.userRepository, Mockito.only()).updateUser(userCaptor.capture());
        verify(this.ytSocketService, Mockito.only()).sendYTLinkUpdate(userCaptor.capture());
        User updated = userCaptor.getValue();
        assertEquals(commentLink, updated.getCommentLink());
        assertEquals(videoLink, updated.getYtVideoLink());
    }


    private void verifyYtServiceCalls(VerificationMode mode) {
        verify(this.ytService, mode).getComment(any(String.class));
        verify(this.ytService, mode).toCommentLink(any(String.class), any(String.class));
        verify(this.ytService, mode).toVideoLink(any(String.class));
    }

    private void verifyInternalServiceCalls(VerificationMode mode) {
        verify(this.userRepository, mode).updateUser(this.user);
        verify(this.ytSocketService, mode).sendYTLinkUpdate(this.user);
    }
}
