package cs.ubbcluj.ro.deliveryservice.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Teo on 04.12.2017.
 */

@Entity(indices = {@Index("name")})
public class DeliveryServiceEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Name: " + name + '\n' +
                "Address: " + address + '\n';
    }
}
