package presentation.dataholder;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class SimpleTagObject {

    private SimpleStringProperty name = new SimpleStringProperty();
    private ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public SimpleTagObject(String name, Color color) {
        this.name.set(name);
        this.color.setValue(color);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }
}
