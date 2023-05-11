package ch.uzh.ifi.hase.soprafs23.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.uzh.ifi.hase.soprafs23.entity.User;

public class MailSenderService {
    private final Logger log = LoggerFactory.getLogger(MailSenderService.class);
    
    public void sendNewPassword(User user) {
      try {
        MailjetRequest request;
        MailjetResponse response;
        MailjetClient client = new MailjetClient(ClientOptions.builder().apiKey(System.getenv("MJ_API")).apiSecretKey(System.getenv("MJ_S")).build());
        request = new MailjetRequest(Emailv31.resource)
          .property(Emailv31.MESSAGES, new JSONArray()
            .put(new JSONObject()
              .put(Emailv31.Message.FROM, new JSONObject()
                .put("Email", "noreply.brainbusters@gmail.com")
                .put("Name", "BrainBusters Team"))
              .put(Emailv31.Message.TO, new JSONArray()
                .put(new JSONObject()
                  .put("Email", user.getEmail())
                  .put("Name", user.getUsername())))
              .put(Emailv31.Message.SUBJECT, "Your password has been reset")
              .put(Emailv31.Message.TEXTPART, "Hello " + user.getUsername() + "!\n\n" +
                "It seems like you forgot your password. Let us help you out.\n\n" +
                "Your new password is: \n\t\t" +
                user.getPassword() + "\n\n" +
                "Please change this password in your settings to one you can remember!\n\n" +
                "All the best,\n" +
                "Your BrainBusters Team")));
        response = client.post(request);
        log.info(String.valueOf(response.getStatus()));
        log.info(String.valueOf(response.getData()));
    } catch (MailjetException e) {
        log.error("MailjetException during request of external API: {}", e.getMessage());
    }
  }
}
