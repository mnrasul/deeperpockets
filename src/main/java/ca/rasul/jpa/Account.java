package ca.rasul.jpa;

import lombok.*;

import javax.persistence.*;

/**
 * @author Nasir Rasul {@literal nasir@rasul.ca}
 */
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Builder (builderMethodName = "build")
@EqualsAndHashCode
@Getter
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "accounts_id_seq")
    @SequenceGenerator(name = "accounts_id_seq", sequenceName = "accounts_id_seq")
    private Long id;

    private String accountNumber;
    private String bankId;
    private String accountType;
    private String institution;
    private String currency;

    public static AccountBuilder builder() {
        return build(); // Replace Builder constructor with _builder()
    }
}
