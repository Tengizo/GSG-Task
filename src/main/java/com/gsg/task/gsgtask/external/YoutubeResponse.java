package com.gsg.task.gsgtask.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"items"
})
public class YoutubeResponse {

@JsonProperty("items")
private List<YoutubeResponseItem> items = null;


@JsonProperty("items")
public List<YoutubeResponseItem> getItems() {
return items;
}

@JsonProperty("items")
public void setItems(List<YoutubeResponseItem> youtubeResponseItems) {
this.items = youtubeResponseItems;
}

}
