package com.order.management.notification.service;

import org.springframework.stereotype.Service;

import com.order.management.common.model.NotificationMessage;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import tools.jackson.databind.ObjectMapper;

@Service
public class NotificationService {
    public void sendNotification(NotificationMessage notificationMessage) {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/021891579865/notification";
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(notificationMessage);

            SqsClient sqsClient = SqsClient.builder()
                    .region(Region.US_EAST_1)
                    .build();

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonPayload)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
