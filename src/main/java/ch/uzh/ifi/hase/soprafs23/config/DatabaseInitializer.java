package ch.uzh.ifi.hase.soprafs23.config;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.TemplateImageQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TemplateImageQuestionRepository templateImageQuestionRepository;

    @Override
    public void run(String... args) {
        createSingletonUser();
        createImageQuestions();
    }

    private void createSingletonUser(){
        User user = new User();
        user.setUsername("singletonUser");
        user.setEmail("singleton@singleton.ch");
        user.setPassword("singletonPassword");
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.ONLINE);
        user.setProfilePicture(user.getUsername());
        user.setPoints(0L);
        user.setId(1L);
        try {
            userRepository.save(user);
            userRepository.flush();
            log.debug("SingletonUser inserted into DB");
        } catch (Exception ex) {
            log.debug("Exception was raised during set up of default user: " + ex.getMessage());
        }
    }


    private void createImageQuestions(){
        String correctAnswer = "Dog";
        List<String> answerList = new ArrayList<>();
        answerList.add("Cat");
        answerList.add("Mouse");
        answerList.add("Hamster");

        TemplateImageQuestion templateImageQuestion = new TemplateImageQuestion();
        templateImageQuestion.setTemplateImageQuestionId(2L);
        templateImageQuestion.setApiId("sjpa0Gg");
        templateImageQuestion.setCorrectAnswer(correctAnswer);
        templateImageQuestion.setIncorrectAnswers(answerList);

        answerList.add(correctAnswer);
        templateImageQuestion.setAllAnswers(answerList);

        templateImageQuestion.setQuestion("What kind of animal do you see?");
        try {
            templateImageQuestionRepository.save(templateImageQuestion);
            templateImageQuestionRepository.flush();
            log.debug("templateImageQuestion inserted into DB");
        } catch (Exception ex) {
            log.debug("Exception was raised during set up of templateImageQuestion: " + ex.getMessage());
        }
    }
}
