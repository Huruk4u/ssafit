package com.example.ssafit.model.dto;

public class SearchCondition {
    // 한 페이지에 몇개나 보여줄 것인지 결정한다.
    public final int countPerPage = 10;

    // 검색 컬럼 (예: title, content, username). 기본은 검색하지 않음
    private String key = "none";

    // 검색어
    private String word;

    // 운동 부위 (예: 하체, 상체 등)
    private String tag;

    // 게시판  (예: 자유게시판, 질문게시판 등)
    private String category;

    // 정렬 컬럼 이름 (예: createdAt, viewCount). 기본은 정렬 안 함
    private String orderBy = "none";

    // 정렬 방향 (asc 또는 desc). 기본은 asc
    private String orderByDir = "asc";

    // 현재 페이지 번호. 기본은 1
    private int currentPage = 1;

    // limit 적용 여부
    private boolean limit = true;

    public SearchCondition() {}

    public SearchCondition(String key, String word) {
        this(key, word, "none");
    }

    public SearchCondition(String key, String word, String orderBy) {
        this(key, word, orderBy, "asc");
    }

    public SearchCondition(String key, String word, String orderBy, String orderByDir) {
        this.key = key;
        this.word = word;
        this.orderBy = orderBy;
        this.orderByDir = orderByDir;
    }

    // offset 계산
    public int getOffset() {
        return (this.currentPage - 1) * countPerPage;
    }

    // Getters and Setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderByDir() {
        return orderByDir;
    }

    public void setOrderByDir(String orderByDir) {
        this.orderByDir = orderByDir;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isLimit() {
        return limit;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "SearchCondition [key=" + key + ", word=" + word + ", tag=" + tag + ", category=" + category +
                ", orderBy=" + orderBy + ", orderByDir=" + orderByDir + ", currentPage=" + currentPage +
                ", limit=" + limit + "]";
    }
}
