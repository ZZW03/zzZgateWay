package com.zzz.test;

import com.zzz.test.controller.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    @Autowired
    private Controller notificationController;

    @Scheduled(fixedRate = 1000) // 每3秒发布一次消息
    public void publishMessage() {
        System.out.println("定时任务");
        String id = "123"; // 假设有一个固定的 ID
        String message = "New notification at " + System.currentTimeMillis();
        notificationController.publishNotification(id, message);
    }
}
