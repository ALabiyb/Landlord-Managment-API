package com.tz.rental.landlord_management.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginatedResponse<T> {
    private T data;
    private Pagination pagination;

    @Data
    @Builder
    public static class Pagination {
        private int currentPage;
        private int totalPages;
        private long totalItems;
        private int itemsPerPage;
    }
}