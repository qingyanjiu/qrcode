package site.moku.qrcodescan.qrcode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.moku.qrcodescan.qrcode.service.RedisService;
import site.moku.qrcodescan.qrcode.utils.QRCodeUtils;
import site.moku.qrcodescan.qrcode.utils.ReachedReserveNumberException;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/qr")
public class GetQRCodeController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @RequestMapping("get")
    public String getQRCode(Model model) {
        try {
            String base64Image = qrCodeUtils.generateBase64Image("1234567", true);
            model.addAttribute("base64Image", base64Image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "qr";
    }

    @RequestMapping("reserve")
    @ResponseBody
    public Map reserve(String date) {
        Map map = new HashMap();
        try {
            String result = redisService.decreaseReserveNumberOfDate(date);
            String scanUrlQRCodeString = "http://localhost:30001/scan/" + result;
            String base64QRCode = qrCodeUtils.generateBase64Image(scanUrlQRCodeString, true);
            map.put("QRCode", base64QRCode);
            map.put("success", true);
        } catch (ReachedReserveNumberException e) {
            map.put("success", false);
        }
        return map;
    }

    @RequestMapping("init")
    @ResponseBody
    public Map init(String date, int total) {
        Map map = new HashMap();
        redisService.initReserveNumberOfDate(date, total);
        map.put("success", true);
        return map;
    }

    @RequestMapping("scan")
    @ResponseBody
    public Map scan(String date, String reserveId) {
        Map map = new HashMap();
        boolean res = redisService.qryIfReserved(date, reserveId);
        if (res) {
            map.put("success", true);
        } else {
            map.put("success", false);
        }
        return map;
    }
}
