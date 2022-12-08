package org.example;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ItemMapper {
    @Select("SELECT * FROM items WHERE item_id = #{itemId}")
    Item getItemByItemId(String itemId);

    @Insert("""
                INSERT INTO
                    items(name, item_id, img_location, price, date_price_changed, scan_id, description)
                VALUES
                    (#{name}, #{itemId}, #{imgLocation}, #{price}, #{priceChanged}, #{scanId}, #{description})
                """)
    void addItem(Item item);

    @Update("UPDATE items SET scan_id = #{arg1} WHERE id = #{arg0}")
    void updateItemScanId(Integer id, Integer scanId);

    @Update("UPDATE items SET price = #{arg1}, scan_id = #{arg2} WHERE id = #{arg0}")
    void updateItemPriceAndScanId(Integer id, Double price, Integer scanId);

    @Select("SELECT name FROM items WHERE scan_id = #{arg0}")
    List<String> getUnavailableProductsNames(int oldScanId);
}
