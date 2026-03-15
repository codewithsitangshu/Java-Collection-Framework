# Java Generics

> **Introduced in:** Java 5
> **Purpose:** Solve problems related to type safety, manual casting, and code duplication.

---

## 1. The Problem Before Generics (Raw Types)

Before Java 5, collections stored everything as `Object`. This caused two major issues:

```java
// ❌ Pre-Generics — Raw ArrayList
List list = new ArrayList();
list.add("Hello");
list.add(42);          // No error — anything goes in!
list.add(3.14);

// Manual casting required on retrieval
String s = (String) list.get(0);   // ✅ Works
String x = (String) list.get(1);   // 💥 ClassCastException at RUNTIME!
```

| Problem | Description |
|---|---|
| **No Type Safety** | Any object could be added without a compile-time error. |
| **Manual Casting** | Every `get()` required an explicit cast. |
| **Runtime Exceptions** | Wrong casts compiled fine but crashed at runtime with `ClassCastException`. |

> 💡 The danger: the bug only appears at **runtime**, not at **compile time** — making it hard to detect early.

---

## 2. Generics to the Rescue

```java
// ✅ With Generics — Type-safe ArrayList
List<String> list = new ArrayList<>();
list.add("Hello");
list.add("World");
// list.add(42);    // ❌ Compile-time error — caught early!

String s = list.get(0);  // No casting needed!
```

Generics allow **classes, interfaces, and methods** to be defined with **type parameters** (placeholders), enabling them to work with different data types while maintaining **compile-time type safety**.

---

## 3. Generic Classes & Interfaces

### Syntax

Place the type parameter in angle brackets `<T>` after the class or interface name.

### Generic Class — Single Type Parameter

```java
public class Box<T> {
    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}

// Usage
Box<Integer> intBox    = new Box<>(42);
Box<String>  strBox    = new Box<>("Hello");
Box<Double>  doubleBox = new Box<>(3.14);

System.out.println(intBox.getValue());    // → 42
System.out.println(strBox.getValue());    // → Hello
```

### Generic Class — Multiple Type Parameters

```java
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key   = key;
        this.value = value;
    }

    public K getKey()   { return key; }
    public V getValue() { return value; }
}

// Usage
Pair<Integer, String> student = new Pair<>(101, "Alice");
System.out.println(student.getKey()   + " → " + student.getValue());
// → 101 → Alice
```

### Generic Interface

```java
public interface Container<T> {
    void add(T item);
    T get(int index);
}

// Implementing with a specific type
public class StringContainer implements Container<String> {
    private List<String> items = new ArrayList<>();

    public void add(String item) { items.add(item); }
    public String get(int index) { return items.get(index); }
}

// Implementing while staying generic
public class GenericContainer<T> implements Container<T> {
    private List<T> items = new ArrayList<>();

    public void add(T item)      { items.add(item); }
    public T get(int index)      { return items.get(index); }
}
```

### Naming Conventions

| Placeholder | Stands For | Typical Usage |
|---|---|---|
| `T` | Type | General-purpose type |
| `E` | Element | Collections (e.g., `List<E>`) |
| `K` | Key | Map keys |
| `V` | Value | Map values |
| `N` | Number | Numeric types |

---

## 4. Bounded Type Parameters

Restrict which types can be passed to a generic parameter using the `extends` keyword.

### Upper Bound — `<T extends SomeClass>`

```java
public class NumberBox<T extends Number> {
    private T value;

    public NumberBox(T value) { this.value = value; }

    public double doubled() {
        return value.doubleValue() * 2;  // Safe — Number has doubleValue()
    }
}

NumberBox<Integer> intBox    = new NumberBox<>(10);
NumberBox<Double>  doubleBox = new NumberBox<>(3.5);
// NumberBox<String> strBox  = new NumberBox<>("Hi");  // ❌ Compile error!

System.out.println(intBox.doubled());    // → 20.0
System.out.println(doubleBox.doubled()); // → 7.0
```

### Multiple Bounds — `<T extends Class & Interface1 & Interface2>`

```java
// T must extend Number AND implement Comparable
public class SortableBox<T extends Number & Comparable<T>> {
    private T value;

    public SortableBox(T value) { this.value = value; }

    public boolean isGreaterThan(T other) {
        return value.compareTo(other) > 0;
    }
}
```

> ⚠️ **Rule:** The **class must always be listed first**, followed by interfaces. Even for interfaces, `extends` is used — not `implements`.

---

## 5. Generic Methods & Constructors

### Generic Method

A method can define its own type parameter **independent of the class**.

```java
public class ArrayUtils {

    // Generic method — works with any type of array
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    // Generic method with return type
    public static <T> T getFirst(List<T> list) {
        return list.get(0);
    }
}

// Usage
Integer[] nums    = {1, 2, 3, 4};
String[]  names   = {"Alice", "Bob", "Carol"};

ArrayUtils.printArray(nums);    // → 1 2 3 4
ArrayUtils.printArray(names);   // → Alice Bob Carol

List<String> fruits = List.of("Mango", "Apple");
System.out.println(ArrayUtils.getFirst(fruits));  // → Mango
```

### Generic Constructor

A **non-generic class** can still have a generic constructor:

```java
public class Box2 {
    private Object value;

    // Generic constructor
    public <T> Box2(T value) {
        this.value = value;
        System.out.println("Stored type: " + value.getClass().getSimpleName());
    }
}

Box2 b1 = new Box2(100);       // → Stored type: Integer
Box2 b2 = new Box2("Hello");   // → Stored type: String
```

### Method Overloading with Generics

```java
public class Printer {

    // Generic version
    public static <T> void display(T item) {
        System.out.println("Generic: " + item);
    }

    // Specific overload — takes priority when Integer is passed
    public static void display(Integer item) {
        System.out.println("Integer-specific: " + item);
    }
}

Printer.display("Hello");  // → Generic: Hello
Printer.display(42);       // → Integer-specific: 42
```

---

## 6. Wildcards (`?`)

Wildcards represent an **unknown type**, commonly used in method parameters.

### Unbounded Wildcard `<?>`

Use when the specific type doesn't matter — **read-only** operations.

```java
public static void printList(List<?> list) {
    for (Object item : list) {
        System.out.print(item + " ");
    }
}

printList(List.of(1, 2, 3));           // → 1 2 3
printList(List.of("A", "B", "C"));    // → A B C

// ❌ Cannot add elements — type is unknown
// list.add("X");  // Compile error
```

### Upper Bounded Wildcard `<? extends T>`

Restricts to `T` or its **subclasses**. Safe for **reading**.

```java
public static double sumList(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) {
        sum += n.doubleValue();
    }
    return sum;
}

System.out.println(sumList(List.of(1, 2, 3)));         // → 6.0  (Integer)
System.out.println(sumList(List.of(1.5, 2.5, 3.0)));  // → 7.0  (Double)
```

### Lower Bounded Wildcard `<? super T>`

Restricts to `T` or its **superclasses**. Safe for **writing/adding**.

```java
public static void addNumbers(List<? super Integer> list) {
    list.add(10);   // ✅ Safe — we know the list holds at least Integer
    list.add(20);
}

List<Number>  numList = new ArrayList<>();
List<Object>  objList = new ArrayList<>();

addNumbers(numList);   // ✅ Number is a superclass of Integer
addNumbers(objList);   // ✅ Object is a superclass of Integer
```

### Wildcard Summary

```
<?>              — Unknown type.  Read-only.       No add allowed.
<? extends T>   — T or subclass.  Read-friendly.   Add restricted.
<? super T>     — T or superclass. Write-friendly.  Add allowed.
```

---

## 7. Type Erasure — How Generics Work Internally

Generics are a **compile-time feature only**. After compilation, the generic type information is **erased** (removed) for backward compatibility with older Java versions.

### What Happens During Compilation

```
Source Code                      After Type Erasure (Bytecode)
─────────────────────────────    ──────────────────────────────
Box<Integer>           →         Box
List<String>           →         List
T (unbounded)          →         Object
T extends Number       →         Number
```

### Example

```java
// What you write
Box<Integer> box = new Box<>(42);
Integer val = box.getValue();

// What the compiler generates (after erasure)
Box box = new Box(42);
Integer val = (Integer) box.getValue();  // Compiler inserts cast automatically
```

> 💡 The JVM only sees `Object` and raw types at runtime. The type safety you get from generics is entirely a **compile-time guarantee**.

---

## 8. Generic Exceptions

Java does **not** support generic exception classes directly due to Type Erasure.

```java
// ❌ Not allowed — compile error
class MyException<T> extends Exception {
    private T data;
}
```

**Why?** At runtime, type info is erased, so the JVM cannot differentiate between `MyException<String>` and `MyException<Integer>` in a `catch` block.

### ✅ Workaround — Generic Constructor on a Non-Generic Exception

```java
public class DataException extends Exception {
    private Object data;

    // Generic constructor
    public <T> DataException(String message, T data) {
        super(message);
        this.data = data;
    }

    public Object getData() { return data; }
}

// Usage
try {
    throw new DataException("Invalid ID", 404);
} catch (DataException e) {
    System.out.println(e.getMessage() + " → " + e.getData());
    // → Invalid ID → 404
}
```

---

## Quick Reference Summary

```
Java Generics
├── Purpose: Type safety + no manual casting + reusable code
│
├── Generic Class       → class Box<T> { ... }
├── Generic Interface   → interface Container<T> { ... }
├── Generic Method      → public static <T> void print(T[] arr)
├── Generic Constructor → public <T> Box2(T value)
│
├── Bounded Types
│   ├── Upper bound  → <T extends Number>
│   └── Multi-bound  → <T extends Class & Interface>
│
├── Wildcards
│   ├── <?>            → Unknown, read-only
│   ├── <? extends T>  → T or subclass, read-friendly
│   └── <? super T>    → T or superclass, write-friendly
│
├── Type Erasure
│   ├── T          → replaced with Object
│   ├── T extends X → replaced with X
│   └── Casts inserted automatically by compiler
│
└── Limitations
    └── Generic exception classes are NOT supported
```