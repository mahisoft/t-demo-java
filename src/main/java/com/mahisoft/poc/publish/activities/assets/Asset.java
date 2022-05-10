package com.mahisoft.poc.publish.activities.assets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private Address address;
    private List<String> images;
    private Instant lastInspectionDate;
}
