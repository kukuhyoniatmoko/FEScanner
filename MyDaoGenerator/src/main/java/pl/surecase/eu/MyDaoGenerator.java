package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

  public static void main(String args[]) throws Exception {
    Schema schema = new Schema(4, "com.foodenak.itpscanner.persistence.db");
    schema.setDefaultJavaPackageDao("com.foodenak.itpscanner.persistence.dao");
    schema.enableKeepSectionsByDefault();

    Entity userEntity = schema.addEntity("UserEntity");
    userEntity.addIdProperty();
    userEntity.addStringProperty("hashId").unique();
    userEntity.addStringProperty("name");
    userEntity.addStringProperty("username").unique();
    userEntity.addStringProperty("email");
    userEntity.addIntProperty("userPrivilegeId");
    userEntity.addStringProperty("accessToken");
    userEntity.addDateProperty("redeemLuckydipAt");
    userEntity.addDateProperty("redeemVoucherAt");
    userEntity.addStringProperty("imageUrl");

    Entity historyEntity = schema.addEntity("HistoryEntity");
    historyEntity.addIdProperty();
    historyEntity.addLongProperty("eventId");
    historyEntity.addDateProperty("lastRedeemDate");
    historyEntity.addToOne(userEntity, historyEntity.addLongProperty("userId").getProperty());

    Entity eventEntity = schema.addEntity("EventEntity");
    eventEntity.addIdProperty();
    eventEntity.addLongProperty("serverId");
    eventEntity.addStringProperty("name");
    eventEntity.addStringProperty("description");
    eventEntity.addStringProperty("slug");
    eventEntity.addDateProperty("startDate");
    eventEntity.addDateProperty("endDate");
    eventEntity.addBooleanProperty("isShown");
    eventEntity.addIntProperty("views");

    Entity eventImage = schema.addEntity("EventImageEntity");
    eventImage.addIdProperty();
    eventImage.addStringProperty("imageUrl");

    eventEntity.addToMany(eventImage, eventImage.addLongProperty("eventId").getProperty());

    for (Entity entity : schema.getEntities()) {
      entity.implementsSerializable();
    }

    new DaoGenerator().generateAll(schema, args[0]);
  }
}
