package ca.rasul;

import ca.rasul.jpa.Account;
import ca.rasul.jpa.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class DeeperpocketsApplication {
    private static final Logger log = LoggerFactory.getLogger(DeeperpocketsApplication.class);
//    @Autowired
//    private AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(DeeperpocketsApplication.class, args);
	}

    @Bean
    public CommandLineRunner demo(AccountRepository accountRepository){
        return parameters -> {
            accountRepository.save(new Account("abc", "bofa", "CHECKING", "111", "USD"));

            log.info(String.valueOf(accountRepository.count()));
        };
    }
}
