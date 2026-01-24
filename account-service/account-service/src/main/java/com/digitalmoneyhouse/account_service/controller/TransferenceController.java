package com.digitalmoneyhouse.account_service.controller;

import com.digitalmoneyhouse.account_service.model.dto.RecipientResponse;
import com.digitalmoneyhouse.account_service.model.dto.TransferenceCreateRequest;
import com.digitalmoneyhouse.account_service.model.dto.TransferenceResponse;
import com.digitalmoneyhouse.account_service.service.TransferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class TransferenceController {

    private final TransferenceService transferenceService;

    @GetMapping("/{accountId}/transferences")
    public ResponseEntity<List<RecipientResponse>> getLastRecipients(@PathVariable Long accountId) {
        return ResponseEntity.ok(transferenceService.getLastRecipients(accountId));
    }

    @PostMapping("/{accountId}/transferences")
    public ResponseEntity<TransferenceResponse> createTransference(@PathVariable Long accountId, @RequestBody TransferenceCreateRequest tRequest) {
        return ResponseEntity.ok(transferenceService.createTransference(accountId, tRequest));
    }
}
