package com.study.domain.price;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceScraper {

    public Integer getAveragePrice(String title) {
        List<Integer> prices = new ArrayList<>();
        WebDriver driver = null;

        try {
            String query = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String url = "https://m.bunjang.co.kr/search/products?q=" + query;

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            driver = new ChromeDriver(options);
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.cPlkrx")));

            List<WebElement> priceElements = driver.findElements(By.cssSelector("div.cPlkrx"));

            for (WebElement element : priceElements) {
                String priceText = element.getText().replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    prices.add(Integer.parseInt(priceText));
                }
                if (prices.size() >= 10) break;
            }

        } catch (Exception e) {
            return 0;

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        if (prices.isEmpty()) {
            return 0;
        }

        int avg = (int) prices.stream().mapToInt(Integer::intValue).average().orElse(0);
        return avg;
    }
}