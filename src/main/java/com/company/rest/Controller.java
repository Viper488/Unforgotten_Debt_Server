package com.company.rest;

import com.company.dto.LoginDto;
import com.company.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/ud-server/")
public class Controller {
    private static final Logger LOGGER  = LoggerFactory.getLogger(Controller.class);

    @Autowired
    UserService userService;

    @CrossOrigin
    @GetMapping(value = "/start")
    public ResponseEntity workingHttp(){
        return new ResponseEntity("dzialam", HttpStatus.OK);
    }

    @GetMapping(value = "/users")
    public ResponseEntity getUsersJson(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @CrossOrigin
    @PostMapping(value = "/login")
    public ResponseEntity loginUser(@RequestBody LoginDto loginDto){
        if(userService.logIn(loginDto)){
            LOGGER.info("---- Logged user: " + userService.getLoggedUser().getName() + " " + userService.getLoggedUser().getSurname()+" ----");
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

//    @CrossOrigin
//    @PostMapping(value = "/registerUser")
//    public ResponseEntity registerUser(@RequestBody AccountData accountData){
//
//
//        return loggingService.register(accountData) ? new ResponseEntity(true,HttpStatus.OK) : new ResponseEntity(HttpStatus.FORBIDDEN);
//    }
//
//    @CrossOrigin
//    @PutMapping(value = "/forgetPasswordIsUser/{nick}")
//    public ResponseEntity forgetPasswordIsUser(@PathVariable String nick){
//
//        Map<String,String> message = loggingService.isUser(nick);
//        return message != null ? new ResponseEntity(message,HttpStatus.OK) : new ResponseEntity(HttpStatus.FORBIDDEN);
//    }
//
//    @CrossOrigin
//    @PutMapping(value = "/forgetPasswordChange")
//    public ResponseEntity forgetPasswordChange(@RequestBody LogginData fpChangeData){
//        loggingService.fpChange(fpChangeData);
//        return  new ResponseEntity(true,HttpStatus.OK);
//    }
//
//    @CrossOrigin
//    @PutMapping(value = "/changePassword")
//    public ResponseEntity changePassword(@RequestBody ChangePasswordDto changePasswordDto){
//        System.out.println("########################################################################################");
//        return loggingService.userChangingPassword(changePasswordDto) ? new ResponseEntity(HttpStatus.OK):new ResponseEntity("Bad password",HttpStatus.FORBIDDEN);
//    }
}
