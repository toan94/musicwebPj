package com.toan.project.stripe;

import com.stripe.exception.StripeException;
import com.toan.project.models.User;
import com.toan.project.repository.UserRepository;
import com.toan.project.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    PaymentService service;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> completePayment(@RequestBody PaymentRequest request) throws StripeException {
        String chargeId= service.charge(request);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();
        Map<String, Object> response = new HashMap<>();
        if (chargeId != null) {
            Long newBalance = currentUser.getCoin() + request.getAmount();
            currentUser.setCoin(newBalance);
            userRepository.save(currentUser);
            response = new HashMap<>();
            response.put("chargeId", chargeId);
            response.put("currentBalance", newBalance/10); //calculate for coin not $
            return new ResponseEntity<Map<String, Object>>(response,HttpStatus.OK);
        }
        response.put("err", "Please check the credit card details entered");
        return new ResponseEntity<Map<String,Object>>(response, HttpStatus.BAD_REQUEST);

//        return chargeId!=null? new ResponseEntity<String>("chargeId: "+chargeId,HttpStatus.OK):
//                new ResponseEntity<String>("Please check the credit card details entered", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleError(StripeException ex) {
        return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getBalance")
    public Long getBalance(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId()).get();

        return currentUser.getCoin()/10;
    }

}