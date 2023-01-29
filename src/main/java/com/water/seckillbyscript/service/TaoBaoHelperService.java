package com.water.seckillbyscript.service;

import com.beust.ah.A;
import com.water.seckillbyscript.entity.TaoBaoSecKillTask;
import com.water.seckillbyscript.utils.Debugger;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: zzy
 * @date: 2022-12-16 10:11
 * @description: 抢钱神器-by赵占阳
 **/
@Service
public class TaoBaoHelperService {

    private AtomicInteger userNo = new AtomicInteger(0);


    /**
     * @Author zzy
     * @Description /api/taobao?time=1    目前只支持整点抢购，输入24小时制时间
     * @Date 14:58 2022/12/16
     * @Param time
     **/
    @Deprecated
    public void taoBaoCartSecKill(String time) throws Exception {

        //浏览器驱动路径
        System.setProperty("webdriver.chrome.driver", "D:\\program files\\GoogleDriver\\chromedriver.exe");
        //设置秒杀时间 暂时支持整点抢购
        LocalDateTime localDateTime = LocalDateTime.now()
                .withHour(Integer.parseInt(time)).withMinute(0).withSecond(0).withNano(0);
        // 打开浏览器
        ChromeDriver browser = initBrowser();

        Actions actions = new Actions(browser);

        // 用户登录
        login(browser);

        // 进入购物车页面
        browser.get("https://cart.taobao.com/cart.htm");

        // 点击选择第一个按钮
        waitElementLoad(() -> {
            return By.xpath("//*[@id=\"J_SelectAll1\"]/div/label");
        }, browser).click();

        while (true) {
            LocalDateTime now = LocalDateTime.now();
            //当前时间
            if (now.isAfter(localDateTime)) {
                waitElementLoad(() -> {
                    return By.xpath("//*[@id=\"J_Go\"]");
                }, browser).click();

                waitElementLoad(() -> {
                    return By.xpath("//*[@id=\"submitOrderPC_1\"]/div[1]/a[2]");
                }, browser).click();
                break;
            } else if (now.plusHours(1).isBefore(localDateTime)) {
                Thread.sleep(1000 * 60 * 60);
            } else if (now.plusMinutes(10).isBefore(localDateTime)) {
                Thread.sleep(1000 * 60 * 10);
            } else if (now.plusMinutes(1).isBefore(localDateTime)) {
                Thread.sleep(1000 * 60);
            }
        }
    }


    public void taoBaoUrlSecKill(TaoBaoSecKillTask task) throws InterruptedException {

        //设置秒杀时间
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        TemporalAccessor temporalAccessor = dateTimeFormatter.parse(task.getTime());
        LocalDateTime taskTime = LocalDateTime.from(temporalAccessor).withNano(0);

        // 解析url
        String realURL = transformURL(task.getUrl());

        //浏览器驱动路径
        System.setProperty("webdriver.chrome.driver", "D:\\program files\\GoogleDriver\\chromedriver.exe");

        //打开浏览器
        ChromeDriver browser = initBrowser();

        Actions actions = new Actions(browser);
        // 用户登录
        login(browser);

        // 进入首页，便于之后刷新保持登录状态
        browser.get("https://www.taobao.com");

        // 是否在抢购页面，0表示不在，0以上表示进入页面的次数，本项目默认想刷新两次
        int reqSecKillPage = 0;

        int printTimes = 0;

        waitElementLoad(() -> {
            return By.className("site-nav-menu-hd");
        }, browser);

        while (true) {
            LocalDateTime now = LocalDateTime.now().plus(80, ChronoUnit.MILLIS);
            if (now.isAfter(taskTime)) {
                browser.navigate().refresh();

                Debugger.printTime("点击前");

                String script =
                "function clickButton() {"
                + "var button = document.querySelector('#submitBlock_1 > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(3) > div:nth-child(2) > span:nth-child(1)');"
                + "if (!button || button.disabled) {"
                + "return;"
                + "}"
                + "button.click();"
                + "clearInterval(intervalId);"
                + "}"
                + "var intervalId = setInterval(clickButton, 64);";

                ((JavascriptExecutor) browser).executeScript(script);

                Debugger.printTime("点击后");

                break;
            } else {
                // 如果已经在3分钟内了
                if (now.plusMinutes(3).isAfter(taskTime)) {
                    // 如果还没有在抢购页面,那么进入抢购页面
                    if (reqSecKillPage == 0){
                        browser.get(realURL);
                        // 等待页面加载完成
                        waitElementLoad(() -> {
                            return By.xpath("//*[@id=\"submitBlock_1\"]/div[1]/div[1]/div[1]/div[3]/div[2]/span[1]");
                        }, browser);
                        reqSecKillPage++;
                        //如果已经在1分钟内了，而已经在抢购页面了（这里还要加一句，并且时间还没到）
                    } else if (reqSecKillPage == 1 && now.plusMinutes(1).isAfter(taskTime)) {
                        // 刷新页面
                        browser.navigate().refresh();
                        reqSecKillPage++;
                    }

                }


                // 二分尽量减少页面的刷新与循环的时间,经过这一坨处理后可以保证时间在3分钟以内
                if (now.plusHours(1).plusMinutes(3).isBefore(taskTime)) {
                    Thread.sleep(1000 * 60 * 60);
                    // 刷新一下页面
                    browser.navigate().refresh();
                } else if (now.plusMinutes(33).isBefore(taskTime)) {
                    Thread.sleep(1000 * 60 * 30);
                    // 刷新一下页面
                    browser.navigate().refresh();
                } else if (now.plusMinutes(18).isBefore(taskTime)) {
                    Thread.sleep(1000 * 60 * 15);
                }else if (now.plusMinutes(10).isBefore(taskTime)) {
                    Thread.sleep(1000 * 60 * 7);
                }else if (now.plusMinutes(7).isBefore(taskTime)) {
                    Thread.sleep(1000 * 60 * 4);
                } else if (now.plusMinutes(3).isBefore(taskTime)) {
                    Thread.sleep(1000 * 60 * 1);
                }
            }
        }
    }

    private String transformURL(String url) {
        return url.replace("https://h5.m.taobao.com/cart/order.html","https://main.m.taobao.com/order/index.html");
    }

    private ChromeDriver initBrowser() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");
        // 禁用浏览器的预渲染
        options.addArguments("--prerender-from-omnibox=disabled");
        // 不加载图片
        options.addArguments("--blink-settings=imagesEnabled=false");
        // 禁用js
        options.addArguments("--disable-javascript");
        // 设置用户数据路径
        options.addArguments("--user-data-dir=C:\\Users\\28961\\AppData\\Local\\Google\\Chrome\\User Data" + userNo.getAndIncrement());
        // 设置启用多线程
        options.addArguments("--use-spdy=no");
        // 设置drive quit以后不关闭
        options.addArguments("--detach=true");
        // 启用最低的策略
        options.setPageLoadStrategy(PageLoadStrategy.NONE);

        // 禁止图片和css加载
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        prefs.put("permissions.default.stylesheet", 2);
        options.setExperimentalOption("prefs", prefs);

        ChromeDriver driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        return driver;
    }


    private void login(ChromeDriver browser) throws InterruptedException {

        if (isLogin(browser)) {
            return;
        }

        //扫码登录
        waitElementLoad(() -> {
            return By.className("icon-qrcode");
        }, browser).click();

        System.out.println("======= 扫码 =======");

        // 等到跳转到了首页
        waitElementLoad(() -> {
            return By.className("site-nav-menu-hd");
        }, browser);

        System.out.println("======= 主页 =======");
    }


    // 判断是否登录了
    private boolean isLogin(ChromeDriver browser) {
        // 进入登录页面
        browser.get("https://login.taobao.com/member/login.jhtml");

        boolean login = false;
        while (true) {
            try {
                if (!login) {
                    browser.findElement(By.className("icon-qrcode"));
                } else {
                    browser.findElement(By.xpath("//*[@id=\"login\"]/div[1]/div[1]/div[7]/button[1]"));
                }
                return login;
            } catch (Exception e) {
                login = !login;
                continue;
            }
        }
    }

    /***
     * @description: 等待直到 Element 加载出来为止
     * @param: xpath
     * @param: browser
     * @author: WtMonster
     */
    public WebElement waitElementLoad(ActForElement actForElement, ChromeDriver browser) {
        while (true) {
            try {
                WebElement element = browser.findElement(actForElement.act());
                return element;
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * @param actForElement
     * @param browser
     * @param sec
     * @return
     * @description: 等待直到 Element 加载出来为止,有超时时间
     */
    public WebElement waitElementLoadWithTimeOut(ActForElement actForElement, ChromeDriver browser, int sec) {
        long start = System.currentTimeMillis();
        long end = start + sec * 1000;
        while (true) {
            try {
                WebElement element = browser.findElement(actForElement.act());
                return element;
            } catch (Exception e) {
                if (System.currentTimeMillis() >= end) return null;
                continue;
            }
        }
    }



}
