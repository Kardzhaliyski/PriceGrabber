package org.example;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Dao {
    private static Dao instance = null;
    private SqlSessionFactory sessionFactory;

    public Dao() throws IOException {
        Properties properties = Resources.getResourceAsProperties("application.properties");
        InputStream in = Resources.getResourceAsStream("mybatis-config.xml");
        sessionFactory = new SqlSessionFactoryBuilder().build(in, properties);
    }

    public static Dao getInstance() {
        if (instance == null) {
            try {
                instance = new Dao();
            } catch (IOException e) {
                throw new RuntimeException(e); //todo
            }
        }

        return instance;
    }


    public Item getItemByItemId(String itemId) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ItemMapper mapper = session.getMapper(ItemMapper.class);
            return mapper.getItemByItemId(itemId);
        }
    }

    public void addItem(Item item) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ItemMapper mapper = session.getMapper(ItemMapper.class);
            mapper.addItem(item);
        }
    }

    public void updateItemScanId(Integer id, Integer scanId) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ItemMapper mapper = session.getMapper(ItemMapper.class);
            mapper.updateItemScanId(id, scanId);
        }
    }

    public void updateItemPriceAndScanId(Integer id, Double price, Integer scanId) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ItemMapper mapper = session.getMapper(ItemMapper.class);
            mapper.updateItemPriceAndScanId(id, price, scanId);
        }
    }

    public List<String> getUnavailableProductsNames(int scanId) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ItemMapper mapper = session.getMapper(ItemMapper.class);
            return mapper.getUnavailableProductsNames(scanId);
        }
    }

    public int getLastScanId() {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ScanMapper mapper = session.getMapper(ScanMapper.class);
            Integer id = mapper.getLastScanId();
            return id == null ? 0 : id;
        }
    }

    public void addScan(ScanData scanData) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            ScanMapper mapper = session.getMapper(ScanMapper.class);
            mapper.addScan(scanData);
        }
    }



}
