package site.moku.qrcodescan.qrcode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import site.moku.qrcodescan.qrcode.utils.ReachedReserveNumberException;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //初始化每天的预约个数
    public void initReserveNumberOfDate(String date,int totalNumber) {
        stringRedisTemplate.opsForValue().setIfAbsent(date, String.valueOf(totalNumber));
    }

    //预约某天，减少一个可预约个数
    public String decreaseReserveNumberOfDate(String date) throws ReachedReserveNumberException {
        long ret = stringRedisTemplate.opsForValue().decrement(date);
        String result = null;
        if(ret < 0) {
            throw new ReachedReserveNumberException("Reached reserve total number on " + date);
        } else {
            result = this.addReserveInfoOfDate(date);
            return result;
        }
    }

    //预约成功，写入某天预约唯一id
    private String addReserveInfoOfDate(String date) {
        String id = new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.SSS").format(new Date());
        stringRedisTemplate.opsForSet().add("reserveid:"+date, id);
        return id;
    }

    //判断是否已经预约
    public boolean qryIfReserved(String date, String reserveId) {
        boolean result = false;
        result = stringRedisTemplate.opsForSet().isMember("reserveid:"+date, reserveId);
        return result;
    }

}
