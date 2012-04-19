package cat.atridas.antagonista.test;

import javax.vecmath.Quat4f;

import cat.atridas.antagonista.Transformation;

public class TestRots {

  public static void main(String args[]) {
    Transformation t = new Transformation();

    Quat4f q = new Quat4f();

    System.out.println("setting yaw pitch roll 1, 0.5, 0.25");
    t.setRotation(1, .5f, .25f);

    t.getRotation(q);
    System.out.println("to quaternion " + q);

    t.setRotation(0, 0, 0);

    t.setRotation(q);

    System.out.println("to yaw pitch roll again: ");
    System.out.println(t.getYaw());
    System.out.println(t.getPitch());
    System.out.println(t.getRoll());

    System.out.println("-----");

    System.out.println("setting yaw: pi");
    t.setRotation((float) Math.PI, 0, 0);
    t.getRotation(q);
    System.out.println("to quaternion " + q);

    System.out.println("-----");

    System.out.println("setting pitch: pi");
    t.setRotation(0, (float) Math.PI, 0);
    t.getRotation(q);
    System.out.println("to quaternion " + q);

    System.out.println("-----");

    System.out.println("setting roll: pi");
    t.setRotation(0, 0, (float) Math.PI);
    t.getRotation(q);
    System.out.println("to quaternion " + q);

    System.out.println("-----");
    System.out.println("-----");
    System.out.println("-----");

    System.out.println("setting yaw: pi/2");
    t.setRotation((float) Math.PI / 2, 0, 0);
    t.getRotation(q);
    System.out.println("to quaternion " + q);

    System.out.println("-----");

    System.out.println("setting pitch: pi/2");
    t.setRotation(0, (float) Math.PI / 2, 0);
    t.getRotation(q);
    System.out.println("to quaternion " + q);

    System.out.println("-----");

    System.out.println("setting roll: pi/2");
    t.setRotation(0, 0, (float) Math.PI / 2);
    t.getRotation(q);
    System.out.println("to quaternion " + q);
  }

}
