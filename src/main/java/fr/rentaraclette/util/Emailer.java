package fr.rentaraclette.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer extends Thread {

	private static Emailer 			instance = null;
	
	private Properties 				props;
	private SmtpAuthenticator 		authentication;
	private Session 				session;

	private String 					from = "rentaraclette@yahoo.com";
	private String 					to;
	private String 					subject;
	private String 					body;
	
	public static final String		SOAP_MAIL = "mail";
	public static final String		SOAP_TO = "to";
	public static final String		SOAP_SUBJECT = "subject";
	public static final String		SOAP_MAILTYPE = "mailType";
	public static final String		SOAP_VARIABLES = "variables";	

	public static void main(String... aArguments ){
		Emailer emailer = Emailer.getInstance();
		//the domains of these email addresses should be valid,
		//or the example will fail:
		emailer.sendEmail("cohensteve@hotmail.fr", "HtmlTest", "email-template", null);
	}

	private Emailer() {
		props = new Properties();
        props.put("mail.smtp.host" , "smtp.mail.yahoo.com");

        //To use TLS
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true");
        //To use SSL
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        
        props.put("mail.debug", "true");

        authentication = new SmtpAuthenticator();
        session  = Session.getDefaultInstance(props, authentication);
	}

	public static Emailer getInstance() {
		if (instance == null)
			instance = new Emailer();
		return instance;
	}
	
	@Override
	public void run() {
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipient(Message.RecipientType.TO, 
                new InternetAddress(to));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            Logger.info("Email was sent!");
        }
        catch(Exception exc) {
//            Logger.error(exc.getMessage());
        	exc.printStackTrace();
        }
	}

	/**
	 * Send a single email.
	 */
	public void sendEmail(String toEmail, String emailSubject, String mailId, Map<String, String> args){
		to = toEmail;
        subject = emailSubject;
        body = getEmailDataAndUpdate(mailId, args);;
		this.start();
	}

	private String getEmailDataAndUpdate(String mailId, Map<String, String> args) {
		String emailStr = "";
		try {
		InputStream is = Emailer.class.getResourceAsStream("/emailtemplates/" + mailId + ".html");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line;
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
	    emailStr = sb.toString();
		
		Set<String> keys = args.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = args.get(key);
			emailStr = emailStr.replaceAll("[[" + key + "]]", value);
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return emailStr;
	}

	public class SmtpAuthenticator extends Authenticator {
		public SmtpAuthenticator() {
		    super();
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
		 String username = "rentaraclette@yahoo.com";
		 String password = "azertyuiop";
		    if ((username != null) && (username.length() > 0) && (password != null) 
		      && (password.length   () > 0)) {

		        return new PasswordAuthentication(username, password);
		    }

		    return null;
		}
	}
} 
