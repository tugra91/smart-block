package com.turkcell.blockmail;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@RestController
public class Deneme2 {

    public static void main(String [] arg) {
        Double value = Double.valueOf(1.1d);
        System.out.println(value / 10.0);

        BigDecimal bigdec = BigDecimal.valueOf(1.1);
        System.out.println(bigdec.divide(BigDecimal.valueOf(10.0)));

    }

    @RequestMapping(value = "/deneme")
    public String getCode(@RequestParam("code") String code) {
        return code;
    }

}
