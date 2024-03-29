package com.revelup.mypage.controller;

import com.revelup.funding.model.dto.DeliveryDTO;
import com.revelup.funding.model.dto.FundingInfoDTO;
import com.revelup.funding.model.dto.InquiryDTO;
import com.revelup.funding.model.dto.InquiryFileDTO;
import com.revelup.mypage.model.service.MypageService;
import com.revelup.notice.model.dto.NoticeDTO;
import com.revelup.pay.model.dto.PayCompletionDTO;
import com.revelup.pay.model.dto.PayDTO;
import com.revelup.user.model.dto.LoginUserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/content/mypage")
@Log4j2
public class MypageController {

    private final MypageService mypageService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    @GetMapping("/updateTrackingNo")
    public String updateTrackingNo() {
        return "/content/mypage/setter-funding-history";
    }

    // 운송장번호 UPDATE
    // @ResponseBody // @ResponseBody return을 문자 그대로 반환하라는 의미 여기서는 사용하지 않음
    @PostMapping("/updateTrackingNo")
    public String updateTrackingNo(@RequestParam("plgCode") int plgCode, @RequestParam("trackingNo") String trackingNo, Model model, FundingInfoDTO fundingInfoDTO){

        DeliveryDTO deliveryDTO = new DeliveryDTO();
        deliveryDTO.setPlgCode(plgCode);
        deliveryDTO.setDelivStat("S");
        deliveryDTO.setTrackingNo(trackingNo);
        model.addAttribute("deliveryDTO", deliveryDTO);

        fundingInfoDTO.getFndCode();

        int result = mypageService.updateTrackingNo(deliveryDTO);

        if(result > 0) {
            System.out.println("성공!!!!!");
        } else {
            System.out.println("실패!!!!!");
        }

        return "redirect:/content/mypage/setter-funding-history";
    }

    // 후원내역조회
    @GetMapping("/getter-ongoing")
    public String selectAllPlgList(Model model, Principal principal) {

        String userId = principal.getName();

        log.info("userId : {}", userId);

        try {
            List<PayDTO> allPlgList = mypageService.selectAllPlgList(userId);
            model.addAttribute("allPlgList", allPlgList);
            log.info("allPlgList: {}", allPlgList);
            return "content/mypage/getter-ongoing";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    // 나의 후원내역1 상세보기 페이지 이동
    @GetMapping("/getter-spons-details1")
    public String selectOnePlg(@RequestParam("plgCode") int plgCode, Model model) {

        log.info("plgCode : {}", plgCode);
        try {
            PayDTO plgDetails = mypageService.selectOne(plgCode);

            if (plgDetails == null) { // 조회 결과가 없는 경우
                log.info("해당 plgCode에 대한 데이터가 존재하지 않습니다: {}", plgCode);
                model.addAttribute("message", "해당 정보를 찾을 수 없습니다."); // 모델에 메시지 추가
            } else { // 조회 결과가 있는 경우
                log.info("plgDetails: {}", plgDetails); // 조회된 후원 내역 로깅
                model.addAttribute("plgDetails", plgDetails); // 모델에 후원 내역 추가
            }
            return "content/mypage/getter-spons-details1"; // 후원 내역 상세보기 페이지로 이동
        } catch (Exception e) { // 예외 처리
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e); // 오류 로깅
            // 예외를 발생시켜 공통 예외 처리 기능을 통해 사용자에게 적절한 응답을 할 수 있도록 함
            throw new RuntimeException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    // details1에서 trackingNo의 null 유무에 따라 delivStat의 상태만 변화되도록 변경!!!
    // 나의 후원내역3 상세보기 페이지 이동(구매확정)
//    @GetMapping("/getter-spons-details3")
//    public String getterSponsDetailsThreePage() {
//        return "content/mypage/getter-spons-details3";
//    }

    // 후원철회 버튼 클릭 후 페이지 이동
    @GetMapping("/getter-refund")
    public String getterRefundPage(Model model, Principal principal) {
        String userId = principal.getName();
        log.info("userId : {}", userId);
        try {
            List<PayDTO> allPlgList = mypageService.selectRefundList(userId);
            model.addAttribute("allPlgList", allPlgList);
            log.info("allPlgList: {}", allPlgList);
            return "content/mypage/getter-refund";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    // 미달성 펀딩 버튼 클릭 시 페이지 이동
    @GetMapping("/failed-funding")
    public String failedFundingPage(Model model, Principal principal) {
        String userId = principal.getName();
        log.info("userId : {}", userId);
        try {
            List<PayDTO> allPlgList = mypageService.selectFailFndList(userId);
            model.addAttribute("allPlgList", allPlgList);
            log.info("allPlgList: {}", allPlgList);
            return "content/mypage/failed-funding";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }


    // 세터의 펀딩 목록
    @GetMapping("/setter-fndList")
    public String setterFndListPage(Model model, Principal principal) {

        String userId = principal.getName();

        try {
            List<FundingInfoDTO> allFndList = mypageService.allFndList(userId);
            model.addAttribute("allFndList", allFndList);
            log.info("allFndList: {}", allFndList);
            return "content/mypage/setter-fndList";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    // 펀딩 상태를 업데이트하는 엔드포인트
//    @PostMapping("/update-funding-status")
//    public void updateFundingStatus(@RequestParam("fndCode") int fndCode) {
//        mypageService.updateFundingStatus(fndCode);
//    }

    @PostMapping("/setter-fndList/{fndCode}")
    public String setterFndListDeletePage(@PathVariable("fndCode") int fndCode) {

        int successAmt = mypageService.getSuccessAmtByFndCode(fndCode);
        FundingInfoDTO fndInfo = new FundingInfoDTO();
        fndInfo.setSuccessAmt(successAmt);
        return "redirect:/content/mypage/setter-fndList"; // 달성액을 포함한 펀딩 정보 반환
    }

    @GetMapping("/setter-notice/{fndCode}")
    public String sttrNotice(@PathVariable("fndCode")int fndCode, Model model) {
        FundingInfoDTO sttrOneFnd = mypageService.sttrOneFnd(fndCode);
        log.info("selectOne");

        model.addAttribute("sttrOneFnd", sttrOneFnd);

        return "content/mypage/setter-notice";
    }

    // 세터의 펀딩목록 상세조회
    @GetMapping("/setter-funding-history/{fndCode}")
    public String setterSelectOneFnd(@PathVariable("fndCode")int fndCode, Model model) {

        log.info("fndCode : " + fndCode);

        FundingInfoDTO selectOneFnd = mypageService.sttrSelectOneFnd(fndCode);
        log.info("selectOne");

        List<PayDTO> plgList = mypageService.plgList(fndCode);

        model.addAttribute("selectOneFnd", selectOneFnd);
        model.addAttribute("plgList", plgList);

        return "content/mypage/setter-funding-history";
    }

    @PostMapping("/delete/{fndCode}")
    @ResponseBody
    public String deleteFnd(@RequestParam("fndCode") int fndCode) {

        log.info("fndCode : " + fndCode);
        mypageService.deleteFnd(fndCode);

        return "redirect:/content/mypage/setter-fndList";
    }

    // 심사대기중인 펀딩
    @GetMapping("/setter-audreadyList")
    public String audReadyList(Model model, Principal principal) {
        try {
            String userId = principal.getName();

            List<FundingInfoDTO> audReadyList = mypageService.audReadyList(userId);
            model.addAttribute("audReadyList", audReadyList);
            log.info("audReadyList: {}", audReadyList);
            return "content/mypage/setter-audreadyList";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    // 반려된 펀딩
    @GetMapping("/setter-refuseList")
    public String refuseList(Model model,  Principal principal) {
        try {
            String userId = principal.getName();

            List<FundingInfoDTO> refuseList = mypageService.refuseList(userId);
            model.addAttribute("refuseList", refuseList);
            log.info("refuseList: {}", refuseList);
            return "content/mypage/setter-refuseList";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    @PostMapping("/insertInq")
    @ResponseBody
    public String insertInq(@RequestParam("fndCode") int fndCode
                            , @ModelAttribute InquiryDTO inquiryDTO
                            , @ModelAttribute InquiryFileDTO inquiryFileDTO, Model model) {

        log.info("fndCode : " + fndCode);

        FundingInfoDTO refuse = mypageService.inqFnd(fndCode); // 펀딩번호로 정보 가져오기
        model.addAttribute("refuse", refuse);

        mypageService.insertInq(fndCode, inquiryDTO, inquiryFileDTO);

        return "redirect:/content/mypage/setter-refuseList";
    }


    //  종료된 펀딩
    @GetMapping("/setter-finishList")
    public String finishList(Model model, Principal principal) {
        try {
            String userId = principal.getName();
            List<FundingInfoDTO> finishList = mypageService.finishList(userId);
            model.addAttribute("finishList", finishList);
            log.info("finishList: {}", finishList);
            return "content/mypage/setter-finishList";
        } catch (Exception e) {
            log.error("페이지를 불러오는 중 오류가 발생했습니다.", e);
            throw new CustomException("페이지를 불러오는 중 오류가 발생했습니다.", e);
        }
    }

    // 찜한 목록 조회 페이지 이동
    @GetMapping("/all-wishlist")
    public String allWishListPage() {
        return "content/mypage/all-wishlist";
    }

    @ExceptionHandler(CustomException.class)
    public String handleException(CustomException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        log.error("CustomException 발생: {}", e.getMessage());
        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class CustomException extends RuntimeException {
        public CustomException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
