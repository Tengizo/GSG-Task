package com.gsg.task.gsgtask.external;

import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class YTService {
    @Value("${yt.api.base.url}")
    private String ytBaseUrl;
    @Value("${yt.trending.video.api.url}")
    private String ytTrendingUrl;
    @Value("${yt.comment.url}")
    private String ytCommentUrl;
    @Value("${yt.video.base.link}")
    private String ytVideoLinkBase;
    @Value("${yt.public.key}")
    private String ytApiKey;
    private final RestTemplate restTemplate;


    public YTService() {
        restTemplate = new RestTemplate();
    }

    /**
     * returns trending videoId for passed region code
     */
    public String getTrendingVideo(String regionCode) {
        return callYoutube(getTrendingVideoApiUrl(regionCode), true);
    }

    /**
     * returns most relevant comment for passed videoId
     */
    public String getComment(String videoId) {
        return callYoutube(getCommentApiUrl(videoId), false);
    }

    /**
     * generates youtube video link from youtube videoID
     */
    public String toVideoLink(String videoId) {
        return UriComponentsBuilder.fromHttpUrl(ytVideoLinkBase)
                .queryParam("v", videoId)
                .toUriString();
    }
    /**
     * generates youtube comment link from youtube videoID and commentId
     */
    public String toCommentLink(String videoId, String commentId) {
        return UriComponentsBuilder.fromHttpUrl(ytVideoLinkBase)
                .queryParam("v", videoId)
                .queryParam("lc", commentId)
                .toUriString();
    }

    private String getTrendingVideoApiUrl(String regionCode) {
        return UriComponentsBuilder.fromHttpUrl(ytBaseUrl + ytTrendingUrl)
                .queryParam("key", ytApiKey)
                .queryParam("part", "id")
                .queryParam("chart", "mostPopular")
                .queryParam("regionCode", regionCode)
                .queryParam("maxResults", 1).toUriString();

    }

    private String callYoutube(String ytURL, boolean rethrow) {
        try {
            YoutubeResponse res = restTemplate.getForObject(ytURL, YoutubeResponse.class);
            if (res != null && res.getItems().size() > 0) {
                return res.getItems().get(0).getId();
            }
            return null;
        } catch (Exception e) {
            if (rethrow)
                throw new AppException(ExceptionType.YOUTUBE_SERVICE_PROBLEM, e.getMessage());
            return null;
        }
    }

    private String getCommentApiUrl(String videoId) {
        return UriComponentsBuilder.fromHttpUrl(ytBaseUrl + ytCommentUrl)
                .queryParam("key", ytApiKey)
                .queryParam("part", "id")
                .queryParam("chart", "mostPopular")
                .queryParam("videoId", videoId)
                .queryParam("order", "relevance")
                .queryParam("maxResults", 1).toUriString();

    }
}
