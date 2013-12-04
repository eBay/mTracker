package com.ebay.build.service.web;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ebay.build.email.MailSenderInfo;
import com.ebay.build.profiler.utils.FileUtils;


@Controller
@RequestMapping("/mail")
public class MailController {
	@RequestMapping(value = "/configuration", method = RequestMethod.GET)
	public String setMailInfo(ModelMap model, HttpSession session) {
		MailSenderInfo sessionInfo = (MailSenderInfo) session.getAttribute("mailInfo"); 
		boolean validated = false;
		if (sessionInfo == null) {
			model.put("mailInfo", new MailSenderInfo());
		} else {
			validated = (Boolean) session.getAttribute("validated");
			if (validated) {
				model.put("mailInfo", sessionInfo);
				model.put("org.springframework.validation.BindingResult.mailInfo", session.getAttribute("result"));
				model.put("validated", true);
				session.removeAttribute("result");
				session.removeAttribute("mailInfo");
				session.removeAttribute("validated");
			} else {	
				model.remove("validated");
				model.put("mailInfo", new MailSenderInfo());
			}
		}
		return "mail_config";
	}
	
	@RequestMapping(value = "/display", method = RequestMethod.POST)
	public String handleRequest(@ModelAttribute("mailInfo") @Valid MailSenderInfo mailInfo,
			BindingResult result, ModelMap model, HttpSession session) {
		if (result.hasErrors()) {
			model.put("mailInfo", mailInfo);
			session.setAttribute("mailInfo", mailInfo);
			session.setAttribute("result", result);
			session.setAttribute("validated", true);
			return "redirect:/mail/configuration";
			
		}
		StringBuffer buffer = new StringBuffer();
		int addressLength = mailInfo.getToAddresses().length;
		for (int i = 0; i < addressLength; i++) {
			if (i < addressLength -1) {
				buffer.append(mailInfo.getToAddresses()[i]);
				buffer.append(";");				
			} else {
				buffer.append(mailInfo.getToAddresses()[i]);
			}
		}
		
		System.out.println("[INFO]:Reset the recipients : " + buffer.toString());
		try {
			File resourceFolder = new File(this.getClass().getResource("/").getFile());
			Map<String, String> map = new HashMap<String, String>();
			map.put("scheduler.email.from", mailInfo.getFromAddress());
			map.put("scheduler.reliability.email.to", buffer.toString());
			map.put("scheduler.pfdash.time", mailInfo.getCronExpression());
			File file = new File(resourceFolder,"application.properties");
			FileUtils.modifyPropertyFile(file, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("mailInfo", mailInfo);
		return "display";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {	
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
		df.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
		binder.registerCustomEditor(MailSenderInfo.class, "toAddresses", new StringArrayPropertyEditor(","));
	}

}