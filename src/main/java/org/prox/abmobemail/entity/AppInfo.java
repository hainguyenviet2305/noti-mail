package org.prox.abmobemail.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "app_info")
@Entity
public class AppInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appId;
    private String appName;
    private String po;
    private String marketing;
    private String leaderMarketing;
    private String leaderPo;
}
