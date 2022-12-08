package org.example;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface ScanMapper {
    @Insert("""
            INSERT INTO
                scans(uri, scan_datetime, new_items, updated_items)
            VALUES
                (#{uri}, #{dateTime}, #{newItems}, #{updatedItems})
            """)
    void addScan(ScanData scanData);

    @Select("""
            SELECT
                id
            FROM
                scans
            ORDER BY
                id DESC
            LIMIT 1
            """)
    Integer getLastScanId();
}
