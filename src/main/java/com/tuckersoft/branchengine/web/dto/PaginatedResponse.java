package com.tuckersoft.branchengine.web.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private int size;

    public PaginatedResponse(List<T> content, int currentPage, long totalElements, int totalPages, int size) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.size = size;
    }

    public List<T> getContent() { return content; }
    public int getCurrentPage() { return currentPage; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getSize() { return size; }
}
