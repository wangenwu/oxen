package com.baren.test;

import com.baren.bison.demo.proto.Mail;
import com.baren.bison.demo.proto.Message;
import com.baren.bison.spring.annotation.AsRPCService;
import org.apache.avro.util.Utf8;
import org.springframework.stereotype.Component;

/**
 * Created by user on 16/11/1.
 */
//@Component
@AsRPCService(port = 8977, serviceInterface = Mail.class)
public class MailImp implements Mail {

    public Utf8 send(Message message) {
        System.out.println("Sending message");
        return new Utf8("Sending message to " + message.getTo().toString()
                + " from " + message.getFrom().toString()
                + " with body " + message.getBody().toString());
//            return new Utf8("server shahahahhhaahh");
    }
}
