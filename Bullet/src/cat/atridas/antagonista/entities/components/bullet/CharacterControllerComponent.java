package cat.atridas.antagonista.entities.components.bullet;

import javax.vecmath.Point3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.entities.BaseComponent;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.GlobalComponent;
import cat.atridas.antagonista.entities.LocalComponent;

public class CharacterControllerComponent extends
    BaseComponent<CharacterControllerComponent> {

  private final Point3f desiredPosition = new Point3f();

  private float characterWidth, characterHeight, stepHeight;

  private float maxSpeed;

  public CharacterControllerComponent(Entity _entity) {
    super(_entity);
  }

  public void init(Point3f _desiredPosition, float _characterWidth,
      float _characterHeight, float _stepHeight, float _maxSpeed) {
    desiredPosition.set(_desiredPosition);

    characterWidth = _characterWidth;
    characterHeight = _characterHeight;
    stepHeight = _stepHeight;
    maxSpeed = _maxSpeed;

    setInitialized();
  }

  public void setDesiredPosition(Point3f _desiredPosition) {
    desiredPosition.set(_desiredPosition);
  }

  public void getDesiredPosition(Point3f desiredPosition_) {
    desiredPosition_.set(desiredPosition);
  }

  public float getCharacterWidth() {
    return characterWidth;
  }

  public float getCharacterHeight() {
    return characterHeight;
  }

  public float getStepHeight() {
    return stepHeight;
  }

  public float getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(float _maxSpeed) {
    assert _maxSpeed >= 0;
    maxSpeed = _maxSpeed;
  }

  @Override
  public void copy(CharacterControllerComponent _other) {
    super.copy(_other);
    desiredPosition.set(_other.desiredPosition);
    characterWidth = _other.characterWidth;
    characterHeight = _other.characterHeight;
    stepHeight = _other.stepHeight;
    maxSpeed = _other.maxSpeed;
  }

  @Override
  public String toString() {
    return "CharacterControllerComponent: [" + characterWidth + ", "
        + characterHeight + ", " + stepHeight + ", " + maxSpeed + "] - "
        + desiredPosition;
  }

  // ////////////////////////////////////////////////////////////////////////////////////////

  public final static class Global extends CharacterControllerComponent
      implements GlobalComponent<CharacterControllerComponent> {

    public Global(Entity _entity) {
      super(_entity);
    }

    @Override
    public Local createLocalCopy() {
      return new Local();
    }

  }

  public final class Local extends CharacterControllerComponent implements
      LocalComponent<CharacterControllerComponent> {

    private Local() {
      super(CharacterControllerComponent.this.getEntity());
      pullChanges();
    }

    @Override
    public void pushChanges() {
      synchronized (CharacterControllerComponent.this) {
        CharacterControllerComponent.this.copy(this);
      }
    }

    @Override
    public void pullChanges() {
      synchronized (CharacterControllerComponent.this) {
        this.copy(CharacterControllerComponent.this);
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////////////////////////

  private final static HashedString componentType = new HashedString(
      "CharacterControllerComponent");

  @Override
  public HashedString getComponentType() {
    return componentType;
  }

  public static HashedString getComponentStaticType() {
    return componentType;
  }

  static {
    Core.getCore().getEntityManager().registerComponentType(Global.class);
  }
}
