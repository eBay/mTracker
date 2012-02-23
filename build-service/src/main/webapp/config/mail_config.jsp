<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Mail Configuration</title>
<link type="text/css" rel="stylesheet" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
<h2>Mail Information Configuration</h2>
	<form:form id="mailInfoForm" method="POST" action="display" commandName="mailInfo">
		<table>
			<tr>
				<td><form:label path="fromAddress">From</form:label></td>
				<td><form:input path="fromAddress" /> 
				<form:errors path="fromAddress" class="error"></form:errors>
				</td>
			</tr>
			<tr>
				<td><form:label path="toAddresses">To</form:label></td>
				<td><form:input path="toAddresses" /> 				
					<c:if test="${not validateSuccess}">		
						<span>please use comma to separate more email addresses</span>						
					</c:if>					
					<form:errors path="toAddresses" class="error"></form:errors>										
				</td>
			</tr>
			<tr>
				<td><form:label path="cronExpression">Cron Expression</form:label></td>
				<td><form:input path="cronExpression" /> 
				<form:errors path="cronExpression" class="error"></form:errors>
				</td>
			</tr>

			<tr>
				<td colspan="2"><input type="submit" value="Submit" /></td>
			</tr>

		</table>
	</form:form>
</body>
</html>