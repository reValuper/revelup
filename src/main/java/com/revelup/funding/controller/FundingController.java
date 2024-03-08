package com.revelup.funding.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/content/funding/insertFunding")
@AllArgsConstructor
public class FundingController {

    @GetMapping("/new-funding-terms-of-use")
    public String insertFND1() {
        return "content/funding/insertFunding/new-funding-terms-of-use";
    }

    @GetMapping("/new-funding-setter-info")
    public String insertFND2() {
        return "content/funding/insertFunding/new-funding-setter-info";
    }

    @GetMapping("/new-funding-basic-info")
    public String insertFND3() {
        return "content/funding/insertFunding/new-funding-basic-info";
    }

    @GetMapping("/new-funding-story")
    public String insertFND4() {
        return "content/funding/insertFunding/new-funding-story";
    }

    @GetMapping("/new-funding-gift-info")
    public String insertFND5() {
        return "content/funding/insertFunding/new-funding-gift-info";
    }

    @GetMapping("/new-funding-complete")
    public String insertFND6() {
        return "content/funding/insertFunding/new-funding-complete";
    }


}