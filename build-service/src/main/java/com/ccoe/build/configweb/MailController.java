/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.configweb;


/*
@Controller
@RequestMapping("/mail")
public class MailController {
	@RequestMapping(value = "/configuration", method = RequestMethod.GET)
	public String setMailInfo(ModelMap model, HttpSession session) {
		MailSenderInfo sessionInfo = (MailSenderInfo) session.getAttribute("mailInfo"); 
		boolean validateSuccess = false;
		if (sessionInfo == null) {
			model.put("mailInfo", new MailSenderInfo());
		} else {
			validateSuccess = (Boolean) session.getAttribute("validateSuccess");
			if (validateSuccess) {
				model.put("mailInfo", sessionInfo);
				model.put("org.springframework.validation.BindingResult.mailInfo", session.getAttribute("result"));
				model.put("validateSuccess", true);
				session.removeAttribute("result");
				session.removeAttribute("mailInfo");
				session.removeAttribute("validateSuccess");
			} else {	
				model.remove("validateSuccess");
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
			session.setAttribute("validateSuccess", true);
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
*/
