package objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class SimpleTagObject {

    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty color = new SimpleStringProperty();

    public SimpleTagObject(String name, Color color) {
        this.name.set(name);
        this.color.set(color.toString());
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getColor() {
        return color.get();
    }

    public SimpleStringProperty colorProperty() {
        return color;
    }
}
