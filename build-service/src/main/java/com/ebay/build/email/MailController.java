package com.ebay.build.email;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ebay.build.profiler.utils.FileUtils;


@Controller
@RequestMapping("/mail")
public class MailController {
	
	
	@RequestMapping(value = "/configuration", method = RequestMethod.GET)
	public ModelAndView setMailInfo(ModelMap model) {
		return new ModelAndView("mail_config", "mailInfo", new MailSenderInfo());
	}
	
	@RequestMapping(value = "/display", method = RequestMethod.POST)
	public ModelAndView handleRequest(@ModelAttribute("mailInfo") @Valid MailSenderInfo mailInfo,
			BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return new ModelAndView("mail_config", "mailInfo", mailInfo);
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
		try {
			File resourceFolder = new File(this.getClass().getResource("/").getFile());
			Map<String, String> map = new HashMap<String, String>();
			map.put("scheduler.email.from", mailInfo.getFromAddress());
			map.put("scheduler.reliability.email.to", buffer.toString());
			map.put("scheduler.pfdash.time", mailInfo.getCronExpression());
			File file = new File(resourceFolder,"application.properties");
			FileUtils.writeToFile(file, FileUtils.modifyPropertyFile(file, map));
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("mailInfo", mailInfo);
		return new ModelAndView("display", "mailInfo", mailInfo);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {	
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
		df.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
	}

}
