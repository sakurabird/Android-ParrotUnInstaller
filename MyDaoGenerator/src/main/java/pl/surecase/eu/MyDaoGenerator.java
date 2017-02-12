package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "greendao");
        Entity entity = schema.addEntity("Apps");
        entity.implementsSerializable();
        entity.addIdProperty().autoincrement();
        entity.addStringProperty("name").notNull();
        entity.addStringProperty("package_name").notNull().unique();
        entity.addLongProperty("package_size").notNull();
        entity.addDateProperty("install_time");
        entity.addBooleanProperty("system_app");
        entity.addBooleanProperty("favorite");
        entity.addDateProperty("favorite_added_time");
        entity.addBooleanProperty("delete");
        entity.addDateProperty("deleted_time");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
