package com.tz.rental.landlord_management.infrastructure.persistence.mapper;

import com.tz.rental.landlord_management.domain.model.aggregate.HouseImage;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseEntity;
import com.tz.rental.landlord_management.infrastructure.persistence.entity.HouseImageEntity;
import org.springframework.stereotype.Component;

@Component
public class HouseImageDomainMapper {

    public HouseImageEntity toEntity(HouseImage domain, HouseEntity houseEntity) {
        if (domain == null) {
            return null;
        }
        HouseImageEntity entity = HouseImageEntity.builder()
                .id(domain.getId())
                .house(houseEntity)
                .fileName(domain.getFileName())
                .fileType(domain.getFileType())
                .fileSize(domain.getFileSize())
                .storagePath(domain.getStoragePath())
                .thumbnailPath(domain.getThumbnailPath())
                .caption(domain.getCaption())
                .isPrimary(domain.isPrimary())
                .displayOrder(domain.getDisplayOrder())
                .uploadedAt(domain.getUploadedAt())
                .build();
        return entity;
    }

    public HouseImage toDomain(HouseImageEntity entity) {
        if (entity == null) {
            return null;
        }
        return HouseImage.builder()
                .id(entity.getId())
                .houseId(entity.getHouse().getId())
                .fileName(entity.getFileName())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .storagePath(entity.getStoragePath())
                .thumbnailPath(entity.getThumbnailPath())
                .caption(entity.getCaption())
                .isPrimary(entity.isPrimary())
                .displayOrder(entity.getDisplayOrder())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }
}
