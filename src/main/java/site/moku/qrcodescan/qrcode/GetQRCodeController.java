package site.moku.qrcodescan.qrcode;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import site.moku.qrcodescan.qrcode.QRCodeUtils;

@Controller
@RequestMapping("/qr")
public class GetQRCodeController {

    @RequestMapping("get")
    public String getQRCode(Model model) {
        try {
            String base64Image = QRCodeUtils.generateBase64Image("1234567","",true);
            model.addAttribute("base64Image",base64Image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "qr";
    }
}
