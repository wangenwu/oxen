package com.baren.test;

import com.baren.bison.demo.proto.Mail;
import com.baren.bison.demo.proto.Message;
import com.baren.bison.spring.annotation.RPCServiceClient;
import org.apache.avro.AvroRemoteException;
import org.apache.avro.util.Utf8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PlayController {

//	@Autowired
//    YksService yksService;

	@RPCServiceClient(port = 8977, host = "127.0.0.1", serviceInterface = Mail.class)
	private Mail mail;

	@RequestMapping(value = "/play", method = RequestMethod.GET)
	public String index(Principal principal,
						@RequestParam(value="to" ,required =false, defaultValue = "to") String to,
						@RequestParam(value="from" ,required =false, defaultValue = "from") String from,
						@RequestParam(value="body" ,required =false, defaultValue = "body00") String body) throws AvroRemoteException {

		Message message = new Message();
		message.setTo(new Utf8(to));
		message.setFrom(new Utf8(from));
		message.setBody(new Utf8(body));
		return mail.send(message).toString();

//		return yksService.getPlay(33, "dddd");
	}
}
