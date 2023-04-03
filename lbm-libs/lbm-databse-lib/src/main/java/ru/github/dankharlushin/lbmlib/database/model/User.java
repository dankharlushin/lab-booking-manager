package ru.github.dankharlushin.lbmlib.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "os_username")
    private String osUsername;
    @Column(name = "os_password")
    private String osPassword;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "study_group")
    private String group;
}

