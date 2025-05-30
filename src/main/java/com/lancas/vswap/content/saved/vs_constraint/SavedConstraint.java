package com.lancas.vswap.content.saved.vs_constraint;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.util.ShipUtil;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.world.ServerShipWorldCore;

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SavedConstraint {
    /*public static class ConstraintSerializer extends JsonSerializer<VSConstraint> {
        VSPipelineSerializer_Factory factory;
        public static void serializeAttachment()
        @Override
        public void serialize(VSConstraint constraint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(constraint.getConstraintType().name());
            VSJacksonUtil.INSTANCE.
        }
    }
    public static class ConstraintDeserializer extends JsonDeserializer<VSConstraint> {
        @Override
        public VSConstraint deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            VSConstraintType type = VSConstraintType.valueOf(jsonParser.getValueAsString());
            switch (type) {
                case ATTACHMENT ->
            }
        }
    }*/

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    private VSConstraint constraint;
    public boolean ship0IsGround;
    public boolean ship1IsGround;

    @JsonIgnore
    private Integer addedID = null;  //null if not added in level

    public boolean isInLevel() { return addedID != null; }
    /*public boolean isLoading() { return !addedToLevel && !failed; }
    public boolean isFailed() { return failed; }
    public boolean isFinish() { return addedToLevel || failed; }*/
    public Integer getAddedID() { return addedID; }
    public VSConstraint getConstraint() { return constraint; }
    public boolean isShip0Ground() { return ship0IsGround; }
    public boolean isShip1Ground() { return ship1IsGround; }

    private SavedConstraint() {}  //for deserialize
    private SavedConstraint(VSConstraint inConstraint, boolean isShip0Ground, boolean isShip1Ground) {
        ship0IsGround = isShip0Ground;
        ship1IsGround = isShip1Ground;

        constraint = inConstraint;
    }
    public static SavedConstraint inLevel(ServerLevel level, VSConstraint inConstraint, @NotNull Integer inAddedID) {
        long groundId = ShipUtil.getGroundId(level);

        boolean isShip0Ground = inConstraint.getShipId0() == groundId;
        boolean isShip1Ground = inConstraint.getShipId1() == groundId;

        if (isShip0Ground && isShip1Ground) {
            EzDebug.warn("there is a constraint whose two shipID are all groundId");
        }

        SavedConstraint inLevel = new SavedConstraint(inConstraint, isShip0Ground, isShip1Ground);
        inLevel.addedID = inAddedID;
        return inLevel;
    }

    public boolean tryAddToLevel(ServerShipWorldCore shipWorld) {
        if (isInLevel()) return true;
        if (constraint == null) {
            EzDebug.error("the constraint is null, fail to load");
            return false;
        }

        addedID = shipWorld.createNewConstraint(constraint);
        EzDebug.log("create with id:" + addedID);
        return addedID != null;
    }
}
