<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Information</title>
</head>
<body>
<h2>Display Configuration </h2>
	<table>
		<tr>
			<td>From</td>
			<td>${mailInfo.fromAddress}</td>
		</tr>
		<tr>
			<td>To</td>
			<td>
				<c:forEach items="${mailInfo.toAddresses}" var="address">
					<c:out value="${address}"></c:out>
				</c:forEach>
			</td>
		</tr>
		
		<tr>
			<td>Cron Expression</td>
			<td>${mailInfo.cronExpression}</td>
		</tr>
	</table>

</body>
</html>