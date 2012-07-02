<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PageSurfer</title>
<link rel="stylesheet" href="style.css" type="text/css"></link>
</head>
<body>

<span id="page">PAGE</span><span id="surfer">Surfer</span>
	
	
		<h5>Just type in a word, hit search and you're ready to surf</h5>
		<div style="color:red">
			<html:errors/>
		</div>
		
			<html:form action="/PageSurferAction">
				<html:text name="PageSurferForm" property="searchWord" size="100"/>
				<html:submit value="Search"/>
			</html:form>
	
	
			<p><bean:write name="PageSurferForm" property="message"/></p>
			<!--<p>Results:</p>
			<bean:write name="PageSurferForm" property="searchWord"/>-->
			
			<!--<logic:present name="isites">
				<logic:iterate id="nsite" name="isites">
					<p><bean:write name="nsite" property="siteTitle"/></p>
					<p><a href="<bean:write name="nsite" property="siteURL"/>"><bean:write name="nsite" property="siteURL"/></a></p>
				</logic:iterate>
			</logic:present>-->
			
			
			<p>Categorized Sorted Results:</p>
			<logic:present name="psites">
				<logic:iterate id="ds" name="psites">
					<bean:define id="nsite" name="ds" property="key"/>
					<bean:define id="smap" name="ds" property="value"/>
					<p><h4><bean:write name="nsite" property="siteTitle"/></h4></p>
					<p><a href="<bean:write name="nsite" property="siteURL"/>"><bean:write name="nsite" property="siteURL"/></a></p>
					<logic:iterate id="cat" name="smap">
						<p class="category"><h5><bean:write name="cat" property="key"/></h5></p>
						<bean:define id="slist" name="cat" property="value"/>
						<logic:iterate id="site" name="slist">
							<p class="catsite"><bean:write name="site" property="siteTitle"/></p>
							<p class="catsite"><a href="<bean:write name="site" property="siteURL"/>"><bean:write name="site" property="siteURL"/></a></p>
						</logic:iterate>
						
					</logic:iterate>
				</logic:iterate>
			</logic:present> 

</body>
</html>