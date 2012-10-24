package no.imr.fishexchange.atlas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CreateChoisesController{

	@RequestMapping("/createchoises")
	public String createchoises() {
		return "choises";
	}
}
