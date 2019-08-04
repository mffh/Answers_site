package com.nowcoder.util;
        import org.apache.velocity.app.VelocityEngine;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.InitializingBean;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.mail.javamail.JavaMailSenderImpl;
        import org.springframework.mail.javamail.MimeMessageHelper;
        import org.springframework.stereotype.Service;
        import org.springframework.ui.velocity.VelocityEngineUtils;

        import javax.mail.internet.InternetAddress;
        import javax.mail.internet.MimeMessage;
        import javax.mail.internet.MimeUtility;
        import java.util.Map;
        import java.util.Properties;

/**
 * Created by mffh on 2019/8/4
 */
@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("xx");
            InternetAddress from = new InternetAddress(nick + "xx");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //邮件发送文件模板
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setPassword("邮箱密码");
        mailSender.setHost("邮箱服务器");
        //mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);//端口
        mailSender.setProtocol("smtps");//邮件密码
        mailSender.setDefaultEncoding("utf8");//编码
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        //javaMailProperties.put("mail.smtp.auth", true);
        //javaMailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
