package com.attica.athens.domain.member.api;

import com.attica.athens.domain.common.ApiResponse;
import com.attica.athens.domain.common.ApiUtil;
import com.attica.athens.domain.member.application.MemberService;
import com.attica.athens.global.auth.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/member")
@RequiredArgsConstructor
public class MemberAuthController {

    private final MemberService memberService;

    /**
     * 사용자 정보 조회 API
     *
     * @param userDetails
     * @return ApiResponse<?>
     */

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                ApiUtil.success(memberService.getMember(userDetails.getUserId()))
        );
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<?>> deleteMember(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        memberService.deleteMember(memberId);
        return ResponseEntity.ok(
                ApiUtil.success(memberService.getMember(userDetails.getUserId()))
        );
    }

    @PostMapping("/{memberId}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> restoreMember(
            @PathVariable Long memberId
    ) {
        memberService.restoreMember(memberId);

        return ResponseEntity.ok(
                ApiUtil.success(memberService.getMember(memberId))
        );
    }
}
