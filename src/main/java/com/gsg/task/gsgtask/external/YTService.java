package com.gsg.task.gsgtask.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public String getTrendingVideo(String regionCode) {
        try {
            Map res = restTemplate.getForObject(getTrendingVideoApiUrl(regionCode), Map.class);
            return firstItemId(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String getComment(String videoId) {
        try {
            Map res = restTemplate.getForObject(getCommentApiUrl(videoId), Map.class);
            return firstItemId(res);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String toLink(String videoId) {
        return UriComponentsBuilder.fromHttpUrl(ytVideoLinkBase)
                .queryParam("v", videoId)
                .toUriString();
    }

    public String toCommentLink(String videoId, String commentId) {
        return UriComponentsBuilder.fromHttpUrl(ytVideoLinkBase)
                .queryParam("v", videoId)
                .queryParam("lc", commentId)
                .toUriString();
    }

    private String firstItemId(Map res) {
        if (res != null) {
            List<Map<String, String>> items = (ArrayList<Map<String, String>>) res.get("items");
            if (items != null && items.size() > 0) {
                String id = items.get(0).get("id");
                return id;
            }
        }
        return null;
    }


    private String getTrendingVideoApiUrl(String regionCode) {
        return UriComponentsBuilder.fromHttpUrl(ytBaseUrl + ytTrendingUrl)
                .queryParam("key", ytApiKey)
                .queryParam("part", "id")
                .queryParam("chart", "mostPopular")
                .queryParam("regionCode", regionCode)
                .queryParam("maxResults", 1).toUriString();

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

    private String addApiKeyParam(String url) {
        return url + "?key=" + this.ytApiKey;
    }


}
