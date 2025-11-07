package edu.citadel.dal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Poi {
    private String name;
    private String type;
    private String address;
    private String reason;
}
