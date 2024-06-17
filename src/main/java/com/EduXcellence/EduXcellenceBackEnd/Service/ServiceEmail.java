package com.EduXcellence.EduXcellenceBackEnd.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceEmail {

        @Autowired
        private JavaMailSender mailSender;

    @Autowired
    private Configuration freemarkerConfig;

    public void sendEmail(String to, String formation, String idformation) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("formation", formation);
        model.put("idformation", idformation);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Template template = freemarkerConfig.getTemplate("email.ftlh");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        helper.setTo(to);
        helper.setSubject("Formation active et commencée");
        helper.setText(text, true);

        // Optionally add attachments
        // helper.addAttachment("attachment.pdf", new ClassPathResource("attachment.pdf"));

            mailSender.send(message);
        }


    public void envoyerEmailAdmin(String sujet, String Contenu) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("Contenu", Contenu);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Template template = freemarkerConfig.getTemplate("emailAdmin.ftlh");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        helper.setTo("mekniamir09@gmail.com");
        helper.setSubject(sujet);
        helper.setText(text, true);

        // Optionally add attachments
        // helper.addAttachment("attachment.pdf", new ClassPathResource("attachment.pdf"));

        mailSender.send(message);
    }

    public void sendEmailFormateur(String to, String formation) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("formation", formation);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Template template = freemarkerConfig.getTemplate("emailFormateur.ftlh");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        helper.setTo(to);
        helper.setSubject("Formation active et commencée");
        helper.setText(text, true);

        // Optionally add attachments
        // helper.addAttachment("attachment.pdf", new ClassPathResource("attachment.pdf"));

        mailSender.send(message);
    }
    }







