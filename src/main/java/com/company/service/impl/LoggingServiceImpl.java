package com.company.service.impl;

import com.company.domain.AccountData;
import com.company.domain.LogginData;
import com.company.domain.UserBase;
import com.company.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingServiceImpl implements LoggingService {

    @Autowired
    private UserBase baseOfUsers;
    @Autowired
    private AccountData currentUser;


//    @Override
//    public boolean register(AccountData data) {
//        System.out.println(data.toString());
//        if(baseOfUsers.findUser(data.getNick())==null)
//        {
//            baseOfUsers.add(data);
//            return true;
//        }
//        return false;
//    }

    @Override
    public boolean logging(LogginData data) {
        currentUser=baseOfUsers.findUser(data.getNick());
        if(currentUser!=null) {
            if(currentUser.getPassword().equals(data.getPassword())){
                return true;
            }
        }
        return false;
    }

//    @Override
//    public Map<String,String> isUser(String nick) {
//        HashMap<String, String> temp = new HashMap<>();
//        currentUser=baseOfUsers.findUser(nick);
//        if(currentUser!=null){
//            temp.put("question", currentUser.getQuestion());
//            temp.put("answer", currentUser.getAnswer());
//            return temp;
//        }
//        return null;
//    }
//
//    @Override
//    public void fpChange(LogginData fpChangeData) {
//        currentUser = baseOfUsers.findUser(fpChangeData.getNick());
//        currentUser.setPassword(fpChangeData.getPassword());
//        baseOfUsers.updateData(currentUser,"password");
//    }
//
//    @Override
//    public boolean userChangingPassword(ChangePasswordDto changePasswordDto) {
//        System.out.println(changePasswordDto.getNick()+changePasswordDto.getNewPassword()+changePasswordDto.getPassword());
//        if(changePasswordDto.getNick().equals(baseOfUsers.findUser(changePasswordDto.getNick()).getNick())){
//            if(changePasswordDto.getPassword().equals(baseOfUsers.findUser((changePasswordDto.getNick())).getPassword())){
//                baseOfUsers.updateData(new AccountData(changePasswordDto.getNick(),changePasswordDto.getNewPassword(),null,null),"password");
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public List<String> getLogins() {
//        ArrayList<AccountData> users = baseOfUsers.getPlatformBase();
//        List<String> logins = new ArrayList<>();
//        for(AccountData temp:users){
//            logins.add(temp.getNick());
//            System.out.println(temp.getNick());
//        }
//        return logins;
//    }
//
//    @Override
//    public void deletePlayer(String nick) {
//        baseOfUsers.deleteUser(nick);
//    }
//
//    }
}
