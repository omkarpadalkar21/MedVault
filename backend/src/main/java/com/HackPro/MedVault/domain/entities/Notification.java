package com.HackPro.MedVault.domain.entities;

import com.HackPro.MedVault.domain.entities.UserManagement.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type; // ACCESS_REQUEST, ACCESS_GRANTED, EMERGENCY_ACCESS, A

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String message;

    private Boolean isRead = false;

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON for additional context

    @CreationTimestamp
    private LocalDateTime createdAt;
}
