package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class QuestionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    private Question question;

    private Question anotherQuestion;

    @BeforeEach
    private void setup() {

        // set up valid question
        this.question = new Question();
        List<String> incorrectAnswers =
                java.util.Arrays.asList(
                        "Neil Young",
                        "Eric Clapton",
                        "Elton John"
                );
        List<String> allAnswers =
                java.util.Arrays.asList(
                        "Neil Young",
                        "Bob Dylan",
                        "Eric Clapton",
                        "Elton John"
                );
        question.setGameId(1L);
        question.setCategory(Category.MUSIC);
        question.setApiId("622a1c357cc59eab6f94ff56");
        question.setCorrectAnswer("Bob Dylan");
        question.setIncorrectAnswers(incorrectAnswers);
        question.setAllAnswers(allAnswers);
        question.setCreationTime(new Date());
        question.setQuestion(
                "Which musician has famously performed over 3,000 shows" +
                " in their 'Never Ending Tour'?"
        );

        // store first question in DB
        entityManager.persist(question);
        entityManager.flush();

        // create second valid question
        this.anotherQuestion = new Question();
        incorrectAnswers = java.util.Arrays.asList(
                "you",
                "allOfUs",
                "WhoKnows?");
        allAnswers = java.util.Arrays.asList(
                "me",
                "you",
                "allOfUs",
                "WhoKnows?"
        );
        anotherQuestion.setGameId(1L);
        anotherQuestion.setCategory(Category.GENERAL_KNOWLEDGE);
        anotherQuestion.setApiId("62433573cfaae40c129614a9");
        anotherQuestion.setCorrectAnswer("me");
        anotherQuestion.setIncorrectAnswers(incorrectAnswers);
        anotherQuestion.setAllAnswers(allAnswers);
        anotherQuestion.setCreationTime(new Date());
        anotherQuestion.setQuestion("Who wrote this test?");

        // store second question in DB
        entityManager.persist(anotherQuestion);
        entityManager.flush();
    }

    @Test
    void findQuestionByQuestionId_success() {
        Question found = questionRepository.findQuestionByQuestionId(question.getQuestionId());

        assertNotNull(found);
        assertEquals(question.getQuestionId(), found.getQuestionId());
        assertEquals(question.getGameId(), found.getGameId());
        assertEquals(question.getCategory(), found.getCategory());
        assertEquals(question.getApiId(), found.getApiId());
        assertEquals(question.getCorrectAnswer(), found.getCorrectAnswer());
        assertEquals(question.getIncorrectAnswers(), found.getIncorrectAnswers());
        assertEquals(question.getAllAnswers(), found.getAllAnswers());
        assertEquals(question.getQuestion(), found.getQuestion());
        assertEquals(question.getCreationTime(), found.getCreationTime());
    }

    @Test
    void findQuestionByQuestionId_noneFound() {
        Question found = questionRepository.findQuestionByQuestionId(-1L);

        assertNull(found);
    }

    @Test
    void findAllByGameId_success() {
        List<Question> found = questionRepository.findAllByGameId(question.getGameId());

        assertNotNull(found);

        assertEquals(2, found.size());

        assertEquals(question.getQuestionId(), found.get(0).getQuestionId());
        assertEquals(question.getGameId(), found.get(0).getGameId());
        assertEquals(question.getCategory(), found.get(0).getCategory());
        assertEquals(question.getApiId(), found.get(0).getApiId());
        assertEquals(question.getCorrectAnswer(), found.get(0).getCorrectAnswer());
        assertEquals(question.getIncorrectAnswers(), found.get(0).getIncorrectAnswers());
        assertEquals(question.getAllAnswers(), found.get(0).getAllAnswers());
        assertEquals(question.getQuestion(), found.get(0).getQuestion());
        assertEquals(question.getCreationTime(), found.get(0).getCreationTime());


        assertEquals(anotherQuestion.getQuestionId(), found.get(1).getQuestionId());
        assertEquals(anotherQuestion.getGameId(), found.get(1).getGameId());
        assertEquals(anotherQuestion.getCategory(), found.get(1).getCategory());
        assertEquals(anotherQuestion.getApiId(), found.get(1).getApiId());
        assertEquals(anotherQuestion.getCorrectAnswer(), found.get(1).getCorrectAnswer());
        assertEquals(anotherQuestion.getIncorrectAnswers(), found.get(1).getIncorrectAnswers());
        assertEquals(anotherQuestion.getAllAnswers(), found.get(1).getAllAnswers());
        assertEquals(anotherQuestion.getQuestion(), found.get(1).getQuestion());
        assertEquals(anotherQuestion.getCreationTime(), found.get(1).getCreationTime());

    }

    @Test
    void findAllByGameId_noneFound() {
        List<Question> found = questionRepository.findAllByGameId(-1);

        assertNotNull(found);
        assertEquals(0, found.size());
    }

    @Test
    void deleteAllByGameId_success() {
        // get all Questions from the DB which are assigned to the gameId
        List<Question> foundQuestionsInDb
                = questionRepository.findAllByGameId(question.getGameId());

        // assert if all assigned questions were found
        assertEquals(2, foundQuestionsInDb.size());

        // execute a deletion of all questions in the DB which were assigned to gameId
        questionRepository.deleteAllByGameId(question.getGameId());

        // get again all questions in DB which were assigned to gameId
        foundQuestionsInDb =
                questionRepository.findAllByGameId(question.getGameId());

        // assert if all questions were deleted
        assertEquals(0, foundQuestionsInDb.size());
    }
}