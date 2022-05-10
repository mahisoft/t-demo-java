package com.mahisoft.poc.crawler.activities;

import lombok.SneakyThrows;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryActivityImpl implements QueryActivity {

    @Override
    @SneakyThrows
    public Page getNextPage(Query query) {
        Random rnd = new Random();

        // Simulating the fetch
        var ids = Stream.generate(() -> UUID.randomUUID().toString())
                .limit(query.size()).collect(Collectors.toList());

        var page = new Page(query.page() < 10, ids);

        // Simulating a query that can response between 0 and 3000 milliseconds
        Thread.sleep(rnd.nextInt(3000));
        return page;
    }

    @Override
    public Item getItem(String id) {
        return new Item(id, String.format("the name of %s", id),
                List.of(
                        String.format("https://images.com/%s/1", id),
                        String.format("https://images.com/%s/2", id),
                        String.format("https://images.com/%s/3", id)
                ));
    }
}
