package com.revpayproject.revpay.service;

import com.revpayproject.revpay.dto.SecurityQuestionDto;
import com.revpayproject.revpay.entity.SecurityQuestion;
import com.revpayproject.revpay.entity.User;
import com.revpayproject.revpay.repository.SecurityQuestionRepository;
import com.revpayproject.revpay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SecurityQuestionService {

    private final UserRepository userRepository;
    private final SecurityQuestionRepository securityQuestionRepository;
    private final PasswordEncoder passwordEncoder;



    public boolean verifyAnswer(String email,
                                String question,
                                String answer) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SecurityQuestion> questions =
                securityQuestionRepository.findByUser(user);

        return questions.stream()
                .anyMatch(q ->
                        q.getQuestion().equals(question) &&
                                passwordEncoder.matches(answer, q.getAnswer()));
    }

    public void updateSecurityQuestions(User user,
                                        List<SecurityQuestionDto> dtos) {

        securityQuestionRepository.deleteAll(
                securityQuestionRepository.findByUser(user));

        for (SecurityQuestionDto dto : dtos) {

            SecurityQuestion question = new SecurityQuestion();
            question.setUser(user);
            question.setQuestion(dto.getQuestion());
            question.setAnswer(passwordEncoder.encode(dto.getAnswer()));

            securityQuestionRepository.save(question);
        }
    }

    public boolean verifySecurityAnswers(User user,
                                         List<SecurityQuestionDto> dtos) {

        List<SecurityQuestion> saved =
                securityQuestionRepository.findByUser(user);

        for (int i = 0; i < saved.size(); i++) {

            if (!passwordEncoder.matches(
                    dtos.get(i).getAnswer(),
                    saved.get(i).getAnswer())) {

                return false;
            }
        }
        return true;
    }
}