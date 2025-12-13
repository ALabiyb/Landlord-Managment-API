package com.tz.rental.landlord_management.domain.model.aggregate;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ContractTemplate {

    private final ContractTemplateId id;
    private String name;
    private String content; // The template content (e.g., HTML, Markdown, or plain text with placeholders)
    private String description;
    private boolean isActive;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public record ContractTemplateId(UUID value) {
        public ContractTemplateId {
            if (value == null) {
                throw new IllegalArgumentException("Contract Template ID cannot be null");
            }
        }
    }

    private ContractTemplate(ContractTemplateId id, String name, String content, String description) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.description = description;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public static ContractTemplate create(String name, String content, String description) {
        return new ContractTemplate(new ContractTemplateId(UUID.randomUUID()), name, content, description);
    }

    public void updateDetails(String name, String content, String description) {
        this.name = name;
        this.content = content;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        validate();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name is required.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Template content is required.");
        }
    }
}