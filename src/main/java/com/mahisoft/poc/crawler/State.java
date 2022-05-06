package com.mahisoft.poc.crawler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class State {

    private int page;
    private int size;
    private int failed;
    private int success;

    public State nextPage() {
        return this.toBuilder().page(page + 1).build();
    }

    public void addSuccess() {
        success++;
    }

    public void addFailed() {
        failed++;
    }
}
