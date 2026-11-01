package com.example.cashier.applications.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload<T> {

    private String recipientName;
    private String phoneNumber;
    private String recipientEmail;
    private String subject;
    private NotificationType type;
    private T data;
    private byte[] attachment;
    private String attachmentFileName;
}
