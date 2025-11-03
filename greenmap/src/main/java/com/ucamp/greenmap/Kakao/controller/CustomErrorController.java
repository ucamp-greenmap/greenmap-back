package com.ucamp.greenmap.Kakao.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // 오류가 발생했을 때 사용자에게 보여줄 페이지를 리턴
        return "error";  // error.html 페이지를 반환 (templates 폴더에 만들어야 함)
    }
}
