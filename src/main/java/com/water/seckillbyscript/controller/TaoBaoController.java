package com.water.seckillbyscript.controller;


import com.water.seckillbyscript.service.TaoBaoHelperService;
import com.water.seckillbyscript.entity.TaoBaoSecKillTask;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**

 * @author: zzy
 * @date: 2022-12-16 11:14
 * @description: controller层
 **/
@RestController
public class TaoBaoController {
    @Resource
    private TaoBaoHelperService taoBaoHelperService;

    @GetMapping("/taobao/cart")
    public void taoBaoHelper(@RequestParam String time) throws Exception {
        taoBaoHelperService.taoBaoCartSecKill(time);
    }

    @PostMapping("/taobao/url")
    public void taoBaoHelper(String url,String time) throws Exception {
        System.out.println("url:" + url);
        System.out.println("time:" + time);
        TaoBaoSecKillTask task = new TaoBaoSecKillTask(url,time);
        taoBaoHelperService.taoBaoUrlSecKill(task);
    }

}
