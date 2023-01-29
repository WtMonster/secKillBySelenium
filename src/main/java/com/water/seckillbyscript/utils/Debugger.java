package com.water.seckillbyscript.utils;

import java.time.LocalDateTime;

/**
 * @author WtMonster
 * @date 2022/12/26 2:48
 */
public class Debugger {

    public static void printTime(String prefix) {
        LocalDateTime res = LocalDateTime.now();
        int second = res.getSecond();
        int nano = res.getNano();
        System.out.println(prefix + ":" + second + "." + nano);
    }

}
