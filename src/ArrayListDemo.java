import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayListDemo {


    public static void main(String[] args) throws Exception {

        List<String> list = new ArrayList<>(11);

        // Adding 10 fruits
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");
        list.add("Date");
        list.add("Elderberry");
        list.add("Fig");
        list.add("Grape");
        list.add("Honeydew");
        list.add("Kiwi");
        list.add("Lemon");

        System.out.println(list);
        System.out.println("Array List size is " + list.size());



        /*Field elementData = ArrayList.class.getDeclaredField("elementData");
        elementData.setAccessible(true);
        Object[] obj = (Object[]) elementData.get(list);
        System.out.println("Array List Capacity is: " + obj.length);*/

        List<String> list1 = Arrays.asList("A", "B", "C");
        //list1.add("D");
        list1.set(1,"D");
        System.out.println(list1);

        List<String> list2 = List.of("A", "B", "C","D");
        System.out.println(list2);

        // List to array
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);

        Object[] array = intList.toArray();
        Integer[] array1 = intList.toArray(new Integer[0]);
        System.out.println(array1[1]);


    }


}
