package com.company.container;

import com.company.bot.MyBot;

import com.company.entity.Question;
import com.company.enums.AdminStatus;
import com.company.enums.UserStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ComponentContainer {
    String BOT_TOKEN="";//bot's token
    String BOT_USERNAME="";//bot's username

    MyBot MY_BOT=new MyBot();
    List<String>MAIN_ADMINS=new ArrayList<>(List.of("5174610361","5181619427"));//main admins chat id

    Map<String, AdminStatus>ADMIN_STATUS=new HashMap<>();
    Map<String,Object >ADMIN_OBJECT=new HashMap<>();

    Map<String, UserStatus>USER_STATUS=new HashMap<>();
    Map<String,Object >USER_OBJECT=new HashMap<>();
    Map<String, List<Question>>USERS_TEST_LIST=new HashMap<>();
    Map<String,Object >PARAM_STATUS=new HashMap<>();

}
