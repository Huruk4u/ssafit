package com.example.ssafit.util;

public class PageNavigation {

    // 현재 페이지
    private int currentPage;
    // 전체 페이지 수
    private int totalPageCount;
    // 전체 데이터 개수
    private int totalCount;
    // 페이지당 데이터 개수
    private int countPerPage;
    // 화면에 보여줄 내비게이션 바의 사이즈
    private int naviSize = 10;
    // 네비게이션 시작 번호
    private int startNavi;
    // 네비게이션 종료 번호
    private int endNavi;
    // 이전 페이지 존재 여부
    private boolean startRange;
    // 다음 페이지 존재 여부
    private boolean endRange;

    public PageNavigation(int currentPage, int totalCount, int countPerPage) {
        this.currentPage = currentPage;
        this.totalCount = totalCount;
        this.countPerPage = countPerPage;

        // 전체 페이지 수 계산
        totalPageCount = (totalCount - 1) / countPerPage + 1;

        // 현재 페이지가 범위를 벗어나면 조정
        if(currentPage < 1) this.currentPage = 1;
        if(currentPage > totalPageCount) this.currentPage = totalPageCount;

        // 네비게이션 바의 시작과 끝 번호 계산
        startNavi = ((this.currentPage - 1) / naviSize) * naviSize + 1;
        endNavi = startNavi + naviSize - 1;
        if(endNavi > totalPageCount) endNavi = totalPageCount;

        // 이전/다음 페이지 존재 여부 설정
        startRange = startNavi > 1;
        endRange = endNavi < totalPageCount;
    }

    // getter, setter 메소드
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public int getNaviSize() {
        return naviSize;
    }

    public void setNaviSize(int naviSize) {
        this.naviSize = naviSize;
    }

    public int getStartNavi() {
        return startNavi;
    }

    public void setStartNavi(int startNavi) {
        this.startNavi = startNavi;
    }

    public int getEndNavi() {
        return endNavi;
    }

    public void setEndNavi(int endNavi) {
        this.endNavi = endNavi;
    }

    public boolean isStartRange() {
        return startRange;
    }

    public void setStartRange(boolean startRange) {
        this.startRange = startRange;
    }

    public boolean isEndRange() {
        return endRange;
    }

    public void setEndRange(boolean endRange) {
        this.endRange = endRange;
    }

    public int getOffset() {
        int page = Math.max(1, this.currentPage);
        int maxPage = Math.max(1, this.getTotalPageCount());
        page = Math.min(page, maxPage);
        return (page - 1) * this.countPerPage;
    }
}

