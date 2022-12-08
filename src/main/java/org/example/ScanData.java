package org.example;

import java.time.LocalDateTime;

public class ScanData {
    public Integer id;
    public String uri;
    public LocalDateTime dateTime;
    public int newItems;
    public int updatedItems;

    public ScanData(String uri) {
        Dao dao = Dao.getInstance();
        id = dao.getLastScanId() + 1;
        this.uri = uri;
        dateTime = LocalDateTime.now();
    }

    public void persist() {
        Dao dao = Dao.getInstance();
        dao.addScan(this);
    }
}
