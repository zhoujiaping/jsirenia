package org.jsirenia.test.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
@RequestMapping("test")
public class TestController {
	@RequestMapping()
	@ResponseBody
	public String testOne(){
		return "hello";
	}
}
