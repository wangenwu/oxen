package com.baren.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PlayController {

	@Autowired
    YksService yksService;

	@RequestMapping(value = "/play", method = RequestMethod.GET)
	public String index(Principal principal) {

		return yksService.getPlay(33, "dddd");
	}
}
