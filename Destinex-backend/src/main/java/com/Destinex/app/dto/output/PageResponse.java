package com.Destinex.app.dto.output;

import com.Destinex.app.entity.Destination;

import java.util.List;


public class PageResponse {
    private List<Destination> content;
    private long totalElements;
    private int elementsPerPage;

    public PageResponse() {
    }

    public PageResponse(List<Destination> content, long totalElements, int elementsPerPage) {
        this.totalElements = totalElements;
        this.content = content;
        this.elementsPerPage = elementsPerPage;
    }

    public List<Destination> getContent() {
        return content;
    }

    public void setContent(List<Destination> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getElementsPerPage() {
        return elementsPerPage;
    }

    public void setElementsPerPage(int elementsPerPage) {
        this.elementsPerPage = elementsPerPage;
    }
}
