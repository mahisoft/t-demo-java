package com.mahisoft.poc.activities;

import lombok.SneakyThrows;

import java.util.Random;

public class RecordActivityImpl implements RecordActivity {

    @Override
    @SneakyThrows
    public void Store(Item item) {
        // Somehow transform the payload into our domain and then do whatever is needed!!!

        // Simulating up to 1000 milliseconds task duration
        Thread.sleep(new Random().nextInt(1000));
    }
}
