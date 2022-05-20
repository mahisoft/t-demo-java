package com.mahisoft.poc.simple;

public class SimpleActivityImpl implements SimpleActivity {
    @Override
    public String callA(String value) {
        return "A";
    }

    @Override
    public String callB(String value) {
        return "B";
    }

    @Override
    public String callC(String a, String b) {
        return "C";
    }
}
