package ru.github.dankharlushin.telegramui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.github.dankharlushin.lbmlib.data.dto.BookingRegistrationResponse;
import ru.github.dankharlushin.telegramui.model.TelegramCallbackResponse;
import ru.github.dankharlushin.telegramui.service.RegistrationCallbackService;

@RestController
@RequestMapping("/callback")
public class RegistrationCallbackController {

    private final RegistrationCallbackService registrationCallbackService;
    private final ObjectMapper objectMapper;

    public RegistrationCallbackController(final RegistrationCallbackService registrationCallbackService,
                                          final ObjectMapper objectMapper) {
        this.registrationCallbackService = registrationCallbackService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> registrationCallback(@RequestBody final BookingRegistrationResponse registrationResponse) {
        final TelegramCallbackResponse callbackResponse = objectMapper.convertValue(registrationResponse.callbackData(),
                TelegramCallbackResponse.class);
        registrationCallbackService.processCallback(callbackResponse, registrationResponse.errorMessage());
        return ResponseEntity.ok().build();
    }
}
