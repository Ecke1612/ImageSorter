import java.util.ArrayList;

public class TestApp {



    public TestApp() {
        ArrayList<Testobj> testobjs = new ArrayList<>();
        Testobj t1 = new Testobj("t1");
        Testobj t2 = new Testobj("t2");
        Testobj t3 = new Testobj("t3");

        testobjs.add(t1);
        testobjs.add(t2);

        ArrayList<Testobj> testobjs2 = new ArrayList<>();
        testobjs2.add(testobjs.get(0));
        testobjs2.add(testobjs.get(1));

        if(testobjs.contains(testobjs2.get(0))) {
            System.out.println("jo, t1 bin drin");
        } else System.out.println("t1 ist nicht dinr");

        if(testobjs.contains(testobjs2.get(1))) {
            System.out.println("jo, t2 bin drin");
        } else System.out.println("t2 ist nicht dinr");

        if(testobjs.contains(t3)) {
            System.out.println("jo, t3 bin drin");
        } else System.out.println("t3 ist nicht dinr");
    }

}
