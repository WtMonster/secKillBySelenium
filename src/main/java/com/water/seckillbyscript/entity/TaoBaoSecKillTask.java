package com.water.seckillbyscript.entity;

/**
 * @author WtMonster
 * @date 2022/12/22 19:08
 */

public class TaoBaoSecKillTask {
    private String url;

    private String time;

    public TaoBaoSecKillTask(String url, String time) {
        this.url = url;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
