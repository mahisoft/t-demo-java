package com.mahisoft.poc.activities;

import java.util.List;

public record Page(boolean hasNext, List<String> ids) {
}
