package eatda.controller.store;

import eatda.controller.web.auth.LoginMember;
import eatda.service.store.CheerService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class CheerController {

    private final CheerService cheerService;

    @PostMapping("/api/cheer")
    public ResponseEntity<CheerResponse> registerCheer(@RequestPart("request") CheerRegisterRequest request,
                                                       @RequestPart(value = "image", required = false) MultipartFile image,
                                                       LoginMember member) {
        CheerResponse response = cheerService.registerCheer(request, image, member.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/api/cheer")
    public ResponseEntity<CheersResponse> getCheers(@RequestParam @Min(1) @Max(50) int size) {
        CheersResponse response = cheerService.getCheers(size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/shops/{storeId}/cheers")
    public ResponseEntity<CheersInStoreResponse> getCheersByStoreId(@PathVariable Long storeId,
                                                                    @RequestParam @Min(1) @Max(50) int size) {
        CheersInStoreResponse response = cheerService.getCheersByStoreId(storeId, size);
        return ResponseEntity.ok(response);
    }
}
