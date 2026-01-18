package com.tz.rental.landlord_management.application.service;

import com.tz.rental.landlord_management.application.dto.NotificationResponse;
import com.tz.rental.landlord_management.application.mapper.NotificationMapper;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationPriority;
import com.tz.rental.landlord_management.domain.model.valueobject.NotificationType;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.LandlordEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.NotificationEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.TenantEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaLandlordRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaNotificationRepository;
import com.tz.rental.landlord_management.infrastructure.persistence.repository.jpa.JpaTenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaNotificationService {

    private final JpaNotificationRepository notificationRepository;
    private final JpaLandlordRepository landlordRepository;
    private final JpaTenantRepository tenantRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void createNotificationForLandlord(UUID landlordId, String title, String message, NotificationType type,
            NotificationPriority priority) {
        LandlordEntity landlord = landlordRepository.findById(landlordId).orElseThrow();

        NotificationEntity notification = new NotificationEntity();
        notification.setLandlord(landlord);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setPriority(priority);

        notificationRepository.save(notification);
    }

    @Transactional
    public void createNotificationForTenant(UUID tenantId, String title, String message, NotificationType type,
            NotificationPriority priority) {
        TenantEntity tenant = tenantRepository.findById(tenantId).orElseThrow();

        NotificationEntity notification = new NotificationEntity();
        notification.setTenant(tenant);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setPriority(priority);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getLandlordNotifications(UUID landlordId, Pageable pageable) {
        return notificationRepository.findByLandlordId(landlordId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
