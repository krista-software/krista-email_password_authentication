/*
 * Email Password Authentication Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package com.krista.extensions.authentication.email_password_authentication.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class EmailSendTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSendTask.class);

    private final String to;
    private final String subject;
    private final String htmlContent;
    private final List<EmailConnection> emailConnections;

    private List<Exception> exceptions;

    EmailSendTask(String to, String subject, String htmlContent, List<EmailConnection> emailConnections) {
        this.to = to;
        this.subject = subject;
        this.htmlContent = htmlContent;
        this.emailConnections = emailConnections;
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public boolean isSuccess() {
        return !emailConnections.isEmpty() && (exceptions == null || exceptions.isEmpty());
    }

    @Override
    public void run() {
        for (EmailConnection connection : emailConnections) {
            if (connection == null) {
                continue;
            }

            if (sendEmail(connection)) {
                return;
            }
        }
    }

    private boolean sendEmail(EmailConnection connection) {
        for (int i = 0; i < connection.getRetryCount(); i++) {
            if (i > 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("Email send retry interrupted for: {}", to);
                    return false;
                }
            }

            try {
                reallySendEmail(connection);
                return true;
            } catch (MessagingException | IOException cause) {
                LOGGER.error("Failed to send email to :{}", to, cause);
                if (exceptions == null) {
                    exceptions = new ArrayList<>();
                }
                exceptions.add(cause);
            }
        }
        return false;
    }

    private void reallySendEmail(EmailConnection emailConnection)
            throws MessagingException, IOException {
        // Create a default MimeMessage object.
        LOGGER.debug("Sending Email to {}", to);
        Message message = getMessage(emailConnection);
        MimeMultipart multipart = getMimeMultipart();
        // Send the actual HTML message, as big as you like
        message.setContent(multipart);
        long startTime = System.currentTimeMillis();
        // Send message
        Transport.send(message);
        long endTime = System.currentTimeMillis();
        LOGGER.debug("Send Email to {} took {}ms", to, endTime - startTime);
    }

    private MimeMultipart getMimeMultipart() throws MessagingException, IOException {
        MimeMultipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlContent, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);

        //appstore image
        if (htmlContent.contains("cid:appstore")) {
            MimeBodyPart appstoreImage = getImage("appstore.png", "appstore");
            multipart.addBodyPart(appstoreImage);
        }

        //facebook image
        if (htmlContent.contains("cid:facebook")) {
            MimeBodyPart facebookImage = getImage("facebook.png", "facebook");
            multipart.addBodyPart(facebookImage);
        }

        //hero image
        if (htmlContent.contains("cid:hero")) {
            MimeBodyPart heroImage = getImage("hero.png", "hero");
            multipart.addBodyPart(heroImage);
        }

        //kristabig image

        if (htmlContent.contains("cid:kristabig")) {
            MimeBodyPart kristabigImage = getImage("kristabig.png", "kristabig");
            multipart.addBodyPart(kristabigImage);
        }

        //kristasmall image
        if (htmlContent.contains("cid:kristasmall")) {
            MimeBodyPart kristasmallImage = getImage("kristasmall.png", "kristasmall");
            multipart.addBodyPart(kristasmallImage);
        }

        if (htmlContent.contains("cid:kristaLogo")) {
            MimeBodyPart kristaLogoImage = getImage("kristaLogo.png", "kristaLogo");
            multipart.addBodyPart(kristaLogoImage);
        }
        if (htmlContent.contains("cid:group")) {
            MimeBodyPart groupImage = getImage("group.png", "group");
            multipart.addBodyPart(groupImage);
        }

        //LinkedIn image
        if (htmlContent.contains("cid:linkedin")) {
            MimeBodyPart linkedinImage = getImage("linkedIn.png", "linkedin");
            multipart.addBodyPart(linkedinImage);
        }

        //playstore image
        if (htmlContent.contains("cid:playstore")) {
            MimeBodyPart playstoreImage = getImage("playstore.png", "playstore");
            multipart.addBodyPart(playstoreImage);
        }

        //twitter image
        if (htmlContent.contains("cid:twitter")) {
            MimeBodyPart twitterImage = getImage("twitter.png", "twitter");
            multipart.addBodyPart(twitterImage);
        }
        return multipart;
    }

    private Message getMessage(EmailConnection emailConnection) throws MessagingException {
        Session session = emailConnection.getSession();
        Message message = new MimeMessage(session);

        String fromUser = emailConnection.getAuthConfig().getFrom();
        if (fromUser == null || fromUser.isBlank()) {
            fromUser = emailConnection.getAuthConfig().getUser(); //fallback to oldsettings
        }
        // Set From: header field of the header.
        message.setFrom(new InternetAddress(fromUser));

        // Set To: header field of the header.
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

        // Set Subject: header field
        message.setSubject(subject);
        return message;
    }

    private File getFile(String fileName, InputStream inputStream) throws IOException {
        File file = new File(fileName);
        try (InputStream is = inputStream;
             OutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }
        return file;
    }

    private MimeBodyPart getImage(String fileName, String fileId)
            throws MessagingException, IOException {
        MimeBodyPart linkedInMessageBodyPart = new MimeBodyPart();
        File linkedInImageFile =
                getFile(fileName, Objects.requireNonNull(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)));
        DataSource fds = new FileDataSource(linkedInImageFile);
        linkedInMessageBodyPart.setDataHandler(new DataHandler(fds));
        linkedInMessageBodyPart.setHeader("Content-ID", "<" + fileId + ">");
        return linkedInMessageBodyPart;
    }

}

