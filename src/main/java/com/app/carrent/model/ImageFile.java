package com.app.carrent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Base64;

@Entity(name = "images")
@Data
@NoArgsConstructor
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private byte[] data;

    public ImageFile(byte[] data) {
        this.data = data;
    }

    public String getImage(){
       return Base64.getEncoder().encodeToString(data);
    }

}
