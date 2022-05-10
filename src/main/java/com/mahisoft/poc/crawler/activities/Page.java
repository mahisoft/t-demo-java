package com.mahisoft.poc.crawler.activities;

import java.util.List;

public record Page(boolean hasNext, List<String> ids) {
}
