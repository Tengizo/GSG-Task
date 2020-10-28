package com.gsg.task.gsgtask.socket.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YTData {
    private String videoLink;
    private String commentLink;
}
