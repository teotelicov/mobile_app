package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Teo on 04.12.2017.
 */

@Entity(indices = {@Index("name")})
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " " ;
    }
}
