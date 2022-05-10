package com.mahisoft.poc.crawler.activities;

import java.util.List;

public record Item(String id, String name, List<String> imagesUrl) {
}
