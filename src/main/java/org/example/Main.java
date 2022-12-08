package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Main {
    public static Set<Character> ILLEGAL_CHARACTERS =
            Set.of('#', '%', '&', '{', '}', '\\',
                    '<', '>', '*', '?', '/', ' ',
                    '$', '!', '\'', '\"', ':', '@',
                    '+', '`', '|', '=');
    public static ScanData scan;
    public static Dao dao;

    public static void main(String[] args) throws URISyntaxException, IOException {
        String url = "https://ardes.bg/laptopi/laptopi";

        scan = new ScanData(url);
        dao = Dao.getInstance();


        int pageNum = 1;
        String oldTitle = null;
        while (true) {
            String currentUrl = url + "/page/" + pageNum++;
            Document document = Jsoup.connect(currentUrl).get();
            String currentTitle = document.title();
            if (currentTitle.equals(oldTitle)) {
                break;
            } else {
                oldTitle = currentTitle;
            }
            extractItemsFromDocument(document);
        }

        int lastScanId = dao.getLastScanId();
        scan.persist();

        List<String> unavailableProducts = dao.getUnavailableProductsNames(lastScanId);
        for (String p : unavailableProducts) {
            System.out.println("Product is unavailable: " + p);
        }
    }

    private static void extractItemsFromDocument(Document document) throws IOException {
        Elements items = document.select(".product");
        for (Element item : items) {
            String itemId = item.attr("data-sku");//id
            itemId = parseItemId(itemId);
            Item itemFromDB = dao.getItemByItemId(itemId);
            if (itemFromDB == null) {
                addNewItem(item, itemId);
                continue;
            }

            Element priceElem = item.selectFirst(".price-num");
            Double price = getPrice(priceElem);
            if (price.equals(itemFromDB.price)) {
                dao.updateItemScanId(itemFromDB.id, scan.id);
                continue;
            }

            dao.updateItemPriceAndScanId(itemFromDB.id, price, scan.id);
            scan.updatedItems++;

            System.out.printf("PRICE CHANGE: %s, Old Price: %.2fлв New Price: %.2fлв%n",
                    itemFromDB.name, itemFromDB.price, price);
        }
    }

    private static void addNewItem(Element itemElement, String itemId) throws IOException {
        String title = itemElement.selectFirst(".title").text();
        String imgFilePath = "src/pictures/" + itemId + ".jpg";
        File file = new File(imgFilePath);
        if (!file.exists()) {
            Files.createFile(Path.of(imgFilePath));
            String imgSrc = itemElement.selectFirst("img.unveil").attr("abs:data-src");
            downloadFile(file, imgSrc);
        }

        StringBuilder sb = new StringBuilder();
        Element parameters = itemElement.selectFirst(".parameters");
        for (Element li : parameters.select("li")) {
            sb.append(li.text()).append(System.lineSeparator());
        }

        String description = sb.toString();

        Element priceElem = itemElement.selectFirst(".price-num");
        Double price = getPrice(priceElem);

        Item item = new Item();
        item.itemId = itemId;
        item.name = title;
        item.imgLocation = imgFilePath;
        item.description = description;
        item.price = price;
        item.priceChanged = LocalDateTime.now();
        item.scanId = scan.id;
        scan.newItems++;

        System.out.printf("NEW ITEM: %s: %.2fлв%n", item.name, item.price);
        dao.addItem(item);
    }

    private static String parseItemId(String itemId) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < itemId.length(); i++) {
            char ch = itemId.charAt(i);
            if (ILLEGAL_CHARACTERS.contains(ch)) {
                continue;
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    private static double getPrice(Element priceElem) {
        String whole = priceElem.ownText();
        String decimal = priceElem.selectFirst(".price-sup").text();
        return Double.parseDouble(whole + "." + decimal);
    }

    private static void downloadFile(File file, String imgSrc) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(Jsoup.connect(imgSrc).ignoreContentType(true).execute().bodyAsBytes());
        }
    }
}