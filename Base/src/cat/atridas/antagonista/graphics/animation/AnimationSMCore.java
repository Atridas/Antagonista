package cat.atridas.antagonista.graphics.animation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;
import cat.atridas.antagonista.Utils;

public class AnimationSMCore extends Resource {
  private static Logger LOGGER = Logger.getLogger(AnimationSMCore.class
      .getCanonicalName());

  /*
   * private final HashMap<HashedString, AnimationInstance>
   * basicLayerAnimationStates = new HashMap<>(); private HashedString
   * basicLayerActiveStateID; private AnimationInstance basicLayerActiveState;
   * private final HashMap<HashedString, Float> basicLayerFadingAnimationWeights
   * = new HashMap<>();
   */

  private final HashMap<HashedString, CoreAnimationState> basicLayerAnimationStates = new HashMap<>();
  private HashedString basicLayerDefaultState;

  public AnimationSMCore(HashedString _resourceName) {
    super(_resourceName);
  }

  @Override
  public boolean load(InputStream is, HashedString extension) {
    try {
      DocumentBuilder db;
      db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = db.parse(is);
      doc.getDocumentElement().normalize();

      Element animationSMXML = doc.getDocumentElement();

      assert animationSMXML.getNodeName().equals("animation_sm");

      Element basicLayerXML = (Element) (animationSMXML
          .getElementsByTagName("basic_layer").item(0));

      assert basicLayerXML != null;
      String defaultState = basicLayerXML.getAttribute("default");
      basicLayerDefaultState = new HashedString(defaultState);

      NodeList animationStatesXML = animationSMXML
          .getElementsByTagName("animation_state");
      for (int i = 0; i < animationStatesXML.getLength(); ++i) {
        Element animationStateXML = (Element) animationStatesXML.item(i);

        String name = animationStateXML.getAttribute("name");
        String l_szFadeIn = animationStateXML.getAttribute("fade_in");
        String l_szFadeOut = animationStateXML.getAttribute("fade_out");

        float fadeIn = Float.parseFloat(l_szFadeIn);
        float fadeOut = Float.parseFloat(l_szFadeOut);

        NodeList animationXML = animationStateXML
            .getElementsByTagName("animation");

        assert animationXML.getLength() == 1;

        CoreAnimationNode animation = getAnimation((Element) animationXML
            .item(0));

        CoreAnimationState animationState = new CoreAnimationState(fadeIn,
            fadeOut, animation);

        basicLayerAnimationStates.put(new HashedString(name), animationState);
      }

      assert basicLayerAnimationStates.containsKey(basicLayerDefaultState);

      // TODO transitions

    } catch (Exception e) {
      LOGGER.warning(Utils.logExceptionStringAndStack(e));
      return false;
    }
    return false;
  }

  private CoreAnimationNode getAnimation(Element animationXML) {
    String type = animationXML.getAttribute("type");

    if (type.equals("single animation")) {
      String id = animationXML.getAttribute("id");

      return new CoreAnimationNode(new HashedString(id));

    } else if (type.equals("lerp animation")) {
      String parameter = animationXML.getAttribute("parameter");

      NodeList animationsXML = animationXML.getElementsByTagName("animation");

      assert animationsXML.getLength() == 2;

      CoreAnimationNode animation1 = getAnimation((Element) animationsXML
          .item(0));
      CoreAnimationNode animation2 = getAnimation((Element) animationsXML
          .item(1));

      return new CoreAnimationNode(animation1, animation2, new HashedString(
          parameter));

    } else if (type.equals("additive animation")) {
      String parameter = animationXML.getAttribute("parameter");
      String animationId = animationXML.getAttribute("animation_id");

      NodeList animationsXML = animationXML.getElementsByTagName("animation");

      assert animationsXML.getLength() == 1;

      CoreAnimationNode baseAnimation = getAnimation((Element) animationsXML
          .item(0));

      return new CoreAnimationNode(new HashedString(animationId),
          baseAnimation, new HashedString(parameter));
    } else {
      throw new IllegalArgumentException(type);
    }
  }

  @Override
  public int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getVRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void cleanUp() {
    assert !cleaned;
    cleaned = true;
  }

  private static enum CoreAnimationStateType {
    SINGLE, LERP, ADDITIVE
  }

  final static class CoreAnimationState {
    final float fadeIn, fadeOut;
    final CoreAnimationNode animationNode;

    CoreAnimationState(float _fadeIn, float _fadeOut,
        CoreAnimationNode _animationNode) {
      fadeIn = _fadeIn;
      fadeOut = _fadeOut;
      animationNode = _animationNode;
    }
  }

  final static class CoreAnimationNode {
    final CoreAnimationStateType type;
    final HashedString animationID, paramID;
    final CoreAnimationNode animation1;
    final CoreAnimationNode animation2;

    // single animation
    CoreAnimationNode(HashedString _animationID) {
      type = CoreAnimationStateType.SINGLE;
      animationID = _animationID;
      animation1 = animation2 = null;
      paramID = null;
    }

    // single animation
    CoreAnimationNode(CoreAnimationNode _animation1,
        CoreAnimationNode _animation2, HashedString parameter) {
      type = CoreAnimationStateType.LERP;
      animationID = null;
      animation1 = _animation1;
      animation2 = _animation2;
      paramID = parameter;
    }

    // additive animation
    CoreAnimationNode(HashedString _animationID, CoreAnimationNode base,
        HashedString parameter) {
      type = CoreAnimationStateType.ADDITIVE;
      animationID = _animationID;
      animation1 = base;
      animation2 = null;
      paramID = parameter;

      throw new RuntimeException("Additive animations not implemented!");
    }
  }
}
