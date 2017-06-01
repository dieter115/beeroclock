package be.flashapps.beeroclock.Models;

import com.google.gson.JsonArray;

/**
 * Created by dietervaesen on 30/05/17.
 */

public class Response {
    private int currentPage;
    private int numberOfPages;
    private int totalResults;
    private JsonArray data;


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public JsonArray getData() {
        return data;
    }

    public void setData(JsonArray data) {
        this.data = data;
    }
}
