package org.prox.abmobemail.service;


import ch.qos.logback.core.util.StringUtil;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prox.abmobemail.dto.EmailModel;
import org.prox.abmobemail.entity.AppInfo;
import org.prox.abmobemail.entity.Email;
import org.prox.abmobemail.entity.Keyword;
import org.prox.abmobemail.entity.UserId;
import org.prox.abmobemail.exception.ResourceNotFoundException;
import org.prox.abmobemail.repository.AppInfoRepository;
import org.prox.abmobemail.repository.EmailRepository;
import org.prox.abmobemail.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
//@AllArgsConstructor
@EnableScheduling
@Slf4j
public class EmailService implements CommandLineRunner {
    private static final String AUTHENTICATE = "AUTHENTICATE";
    @Autowired
    private final EmailRepository emailRepository;
    @Autowired
    private final KeywordRepository keywordRepository;
    @Autowired
    private final DiscordService discordService;
    @Autowired
    private UserIdService userIdService;
    @Autowired
    private final AppInfoRepository appInfoRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public EmailService(EmailRepository emailRepository, KeywordRepository keywordRepository, DiscordService discordService, AppInfoRepository appInfoRepository) {
        this.emailRepository = emailRepository;
        this.keywordRepository = keywordRepository;
        this.discordService = discordService;
        this.appInfoRepository = appInfoRepository;
    }

    private int notiFlag = 0;
    private int maxFail = 10;

    public Page<Email> findAll(Pageable pageable) {
        return emailRepository.findAll(pageable);
    }

    public Email create(Email email) {
        return emailRepository.save(email);
    }

    public Email update(Long id, Email email) {
        Optional<Email> existingEmail = emailRepository.findById(id);
        if (existingEmail.isPresent()) {
            Email updatedEmail = existingEmail.get();
            updatedEmail.setEmailAccount(email.getEmailAccount());
            updatedEmail.setEmailForward(email.getEmailForward());
            updatedEmail.setPw(email.getPw());
            return emailRepository.save(updatedEmail);
        } else {
            throw new ResourceNotFoundException("Email not found with id " + id);
        }
    }

    public void delete(Long id) {
        emailRepository.deleteById(id);
    }

    public Session connectionOutLook(Email email) throws Exception {
        Properties prop = new Properties();
        prop.put("mail.store.protocol", "imaps");
        prop.put("mail.imaps.host", "imap-mail.outlook.com");
        prop.put("mail.imaps.port", "993");
        prop.put("mail.imaps.ssl.enable", "true");
        prop.put("mail.imaps.auth", "true");
        return Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email.getEmailForward(), email.getPw());
            }
        });
    }

    @Scheduled(cron = "0 5 10,23 * * ?")
    public void sentNotiAcitveMail() {
        StringBuilder sb = new StringBuilder("------Check system is active------\n");
        for (Email e : emailRepository.findAll()) {
            sb.append("✅ ");
            sb.append(e.getEmailAccount());
            sb.append(" ");
            sb.append(e.getEmailForward());
            sb.append("\n");
        }
        discordService.sendNotification(String.valueOf(sb));
    }

    @Override
    public void run(String... args) throws Exception {
//        senNoti();
    }

    @Scheduled(fixedRate = 600000) // 120000 milliseconds = 2 minutes
    public void senNoti() throws Exception {
        List<AppInfo> appInfos = appInfoRepository.findAll();
        List<Keyword> keywords = keywordRepository.findAll();
        List<UserId> userIds = userIdService.getAllUsers();
        for (Email email : emailRepository.findAll()) {
            log.info("CHECK: email: {}", email.getEmailForward());
//            if (email.getEmailForward().equalsIgnoreCase("nowtechnoti@hotmail.com"))
            sendNoti(email, appInfos, keywords, userIds);
        }
    }

    private void sendNoti(Email email, List<AppInfo> appInfos, List<Keyword> keywords, List<UserId> userIds) throws Exception {
        Session session = connectionOutLook(email);
        Store store = session.getStore("imaps");
        try {
            store.connect("imap-mail.outlook.com", email.getEmailForward(), email.getPw());
            notiFlag = 0; // Đặt lại cờ khi kết nối thành công
        } catch (Exception e) {
            log.error("(senNoti) error: {}", e.getMessage());
//            sendNoti(email, appInfos, keywords, userIds);
            notiFlag++;
            if (notiFlag >= maxFail) {
                sendError(email.getEmailForward(), e.getMessage());
                notiFlag = 0; // Đặt lại cờ sau khi gửi thông báo lỗi
                log.error("1");
            }
            return;
        }

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        for (Message m : messages) {
            notiEmail(m, appInfos, keywords, userIds);
        }
        inbox.close(false);
        store.close();
    }

    private void sendError(String emailForward, String message) {
        discordService.sendNotification("Failed to connect to email server for user " + emailForward + ": " + message);
    }

    public void notiEmail(Message message, List<AppInfo> appInfos, List<Keyword> keywords, List<UserId> userIds) throws Exception {
        log.info("(notiEmail) subject: {} ", message.getSubject());
        String emailContent = getMessageContent(message);
        checkAppIdInContent(emailContent, appInfos);

        for (Keyword keyword : keywords) {
            if (message.getSubject().contains(keyword.getKeyword()) || message.getSubject().contains(keyword.getTitle())) {

                EmailModel emailModel = new EmailModel();
                emailModel.setTypeMesseage("Limit Admob");
                emailModel.setFromFw(Arrays.toString(message.getFrom()));
                emailModel.setFrom(extractWordsAfterPhrase(emailContent, "From: Google AdMob"));
                emailModel.setSentDate(dateFormat.format(message.getSentDate()));
                emailModel.setSubject(message.getSubject());
                AppInfo appInfo = checkAppIdInContent(emailContent, appInfos);
                if (appInfo != null) {
                    emailModel.setAppId(appInfo.getAppId()); // Gán appId nếu tìm thấy
                    emailModel.setAppName(appInfo.getAppName());
                } else {
                    // Nếu không tìm thấy appId, tìm kiếm theo các cụm từ đã định nghĩa
                    String appId = extractWordsAfterPhrase(emailContent, "App Id:", "Mã ứng dụng:", "Id ứng dụng:", "App Id", "Mã ứng dụng", "Id ứng dụng");
                    appId = appId.substring(appId.indexOf(" ") + 1);
                    emailModel.setAppId(appId);
                    emailModel.setAppName("APP không tồn tại trong hệ thống, vui lòng bổ sung thông tin app vào hệ thống");
                    if (StringUtils.isBlank(emailModel.getAppId()))
                        emailModel.setAppId(emailContent);
                }

                String poUser = getDiscordId(appInfo != null ? appInfo.getPo() : "", userIds);
                String leaderPoUser = getDiscordId(appInfo != null ? appInfo.getLeaderPo() : "", userIds);
                String leaderMarketingUser = getDiscordId(appInfo != null ? appInfo.getLeaderMarketing() : "", userIds);
                String marketingUser = getDiscordId(appInfo != null ? appInfo.getMarketing() : "", userIds);

                discordService.sendNotification(
                        emailModel + "\n" + // Add emailModel and a newline
                                "🆘 @everyone " +
                                " <@" + poUser + ">" +
                                " <@" + leaderPoUser + ">" +
                                " <@" + leaderMarketingUser + ">" +
                                (marketingUser != null ? " <@" + marketingUser + ">" : "")
                );


                // Đánh dấu email là đã đọc
                message.setFlag(Flags.Flag.SEEN, true);
            }
        }
    }

    private String getDiscordId(String name, List<UserId> userIds) {
        Optional<UserId> userIdOptional = userIds.stream()
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst();
        return userIdOptional.isPresent()
                ? userIdOptional.get().getDiscordId()
                : name;
    }


    public String getMessageContent(Message message) throws Exception {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof Multipart) {
            StringBuilder sb = new StringBuilder();
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                sb.append(bodyPart.getContent().toString());
            }
            return sb.toString();
        }
        return "";
    }

    public String extractWordsAfterPhrase(String content, String... phrases) {
        for (String phrase : phrases) {
            int index = content.toLowerCase().indexOf(phrase.toLowerCase());
            if (index != -1) {
                int startIndex = index + phrase.length();
                int endIndex = content.indexOf("\n", startIndex);
                if (endIndex == -1) {
                    endIndex = content.length();
                }
                return content.substring(startIndex, endIndex).trim();
            }
        }
        return "";
    }

    public AppInfo checkAppIdInContent(String emailContent, List<AppInfo> appInfos) {
        logger.info("Email content: {}", emailContent);
        for (AppInfo appInfo : appInfos) {
            String appId = appInfo.getAppId();
            if (emailContent.contains(appId)) {
                logger.info("AppId found: {}", appId);
                return appInfo;
            }
        }
        logger.info("No AppId found in email content.");
        return null;
    }


}
