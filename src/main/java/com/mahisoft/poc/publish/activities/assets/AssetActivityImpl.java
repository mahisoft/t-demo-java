package com.mahisoft.poc.publish.activities.assets;

public class AssetActivityImpl implements AssetActivity {
    @Override
    public Asset getAsset(String id) {

        //TODO: this is a DB call, or a call to an external service

        return Asset.builder()
                .address(Address.builder()
                        .line1("123 Fake Avenue Suit 567")
                        .city("San Diego")
                        .state("CA")
                        .zipCode("12345-1234")
                        .build())
                .build();
    }
}
