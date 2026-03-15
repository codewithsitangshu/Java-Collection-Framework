# Comparator in Java

---

## 1. Overview

A `Comparator` is an interface in `java.util` that enables **custom ordering** of objects during sorting.

| Approach | How to Sort | When to Use |
|---|---|---|
| **Natural Order** | Pass `null` to sort method | Primitives, Strings, or classes implementing `Comparable` |
| **Custom Order** | Pass a `Comparator` | Descending order, sorting by object attributes, complex logic |

Since `Comparator` is a **functional interface** (has exactly one abstract method), it can be implemented via:
- A separate class
- An anonymous class
- A **Lambda expression** ✅ (most concise)

---

## 2. The `compare()` Method

The heart of `Comparator` is the `compare(T o1, T o2)` method. The **integer value returned** tells the sorting algorithm how to arrange the two elements:

```
compare(o1, o2) returns:
  Negative  →  o1 comes BEFORE o2
  Zero      →  o1 and o2 are considered EQUAL (maintain original order)
  Positive  →  o1 comes AFTER o2
```

### Common Patterns

```java
// Ascending order (natural)
(o1, o2) -> o1 - o2        // o1 < o2 → negative → o1 first

// Descending order
(o1, o2) -> o2 - o1        // o1 > o2 → negative → o1 first
```

### Visualizing the Logic

```
List = [5, 2, 8, 1]

compare(5, 2) with o1 - o2 → 5 - 2 = 3 (positive) → swap → [2, 5, 8, 1]
...sorted ascending → [1, 2, 5, 8]

compare(5, 2) with o2 - o1 → 2 - 5 = -3 (negative) → no swap → [5, 2, 8, 1]
...sorted descending → [8, 5, 2, 1]
```

---

## 3. Sorting Basic Types with Comparator

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3));

// Ascending (natural order)
numbers.sort(null);
// → [1, 2, 3, 5, 8, 9]

// Descending using lambda
numbers.sort((o1, o2) -> o2 - o1);
// → [9, 8, 5, 3, 2, 1]

// Using Collections.sort
Collections.sort(numbers, (o1, o2) -> o2 - o1);
// → [9, 8, 5, 3, 2, 1]
```

---

## 4. Sorting Custom Objects — The Student Example

When sorting a list of **custom objects**, passing `null` throws an exception because Java doesn't know the default sorting criteria for your class.

```java
public class Student {
    private String name;
    private double gpa;

    public Student(String name, double gpa) {
        this.name = name;
        this.gpa = gpa;
    }

    public String getName() { return name; }
    public double getGpa()  { return gpa; }

    @Override
    public String toString() {
        return name + " (" + gpa + ")";
    }
}
```

```java
List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 3.8));
students.add(new Student("Bob",   3.5));
students.add(new Student("Carol", 3.9));
students.add(new Student("Dave",  3.5));  // Same GPA as Bob

// ❌ This throws ClassCastException — Java doesn't know how to compare Students
students.sort(null);
```

### Sorting by GPA — Descending (Manual Logic)

Since GPA is a `double`, we cannot use simple subtraction. Instead, compare explicitly:

```java
Comparator<Student> byGpaDesc = (o1, o2) -> {
    if (o2.getGpa() > o1.getGpa()) return 1;   // o2 has higher GPA → o1 comes after
    if (o2.getGpa() < o1.getGpa()) return -1;  // o1 has higher GPA → o1 comes first
    return 0;                                   // Equal GPA → maintain original order
};

students.sort(byGpaDesc);
// → [Carol (3.9), Alice (3.8), Bob (3.5), Dave (3.5)]
```

> 💡 **Stability:** When two elements return `0`, the sorting algorithm preserves their **original relative order**. Bob and Dave both have GPA 3.5 — Bob stays before Dave because he was inserted first.

---

## 5. Modern Comparator API (Java 8+)

Java 8 introduced cleaner, chainable ways to build Comparators.

### `Comparator.comparing()` — Sort by Attribute

```java
// Sort by GPA ascending
students.sort(Comparator.comparing(Student::getGpa));
// → [Bob (3.5), Dave (3.5), Alice (3.8), Carol (3.9)]
```

### `.reversed()` — Flip to Descending

```java
// Sort by GPA descending
students.sort(Comparator.comparing(Student::getGpa).reversed());
// → [Carol (3.9), Alice (3.8), Bob (3.5), Dave (3.5)]
```

### `.thenComparing()` — Secondary / Tie-Breaking Sort

Used when two elements are equal on the primary sort key.

```java
// Sort by GPA descending, then by Name ascending for equal GPAs
students.sort(
    Comparator.comparing(Student::getGpa)
              .reversed()
              .thenComparing(Student::getName)
);
// → [Carol (3.9), Alice (3.8), Bob (3.5), Dave (3.5)]
//                                          ↑ Bob before Dave (alphabetical)
```

---

## 6. String Comparisons

### Lexicographical (Alphabetical) Order

```java
List<String> fruits = new ArrayList<>(Arrays.asList("Banana", "Apple", "Mango", "Fig"));

// Ascending alphabetical (natural)
fruits.sort(null);
// → [Apple, Banana, Fig, Mango]

// Using compareTo explicitly
fruits.sort((o1, o2) -> o1.compareTo(o2));   // ascending
fruits.sort((o1, o2) -> o2.compareTo(o1));   // descending
```

### Sort by String Length

```java
// Ascending by length
fruits.sort((o1, o2) -> o1.length() - o2.length());
// → [Fig, Apple, Mango, Banana]

// Descending by length
fruits.sort((o1, o2) -> o2.length() - o1.length());
// → [Banana, Apple, Mango, Fig]
```

---

## 7. Two Ways to Apply a Comparator

```java
Comparator<Student> comp = Comparator.comparing(Student::getGpa).reversed();

// Option 1 — directly on list instance
students.sort(comp);

// Option 2 — via Collections utility class
Collections.sort(students, comp);
```

Both achieve the same result. `list.sort()` is generally preferred as it is more concise.

---

## Quick Reference Summary

```
Comparator<T>
├── Core method: compare(T o1, T o2) → int
│   ├── Negative → o1 comes first
│   ├── Zero     → equal (stable: preserve original order)
│   └── Positive → o2 comes first
│
├── Common patterns
│   ├── Ascending  : (o1, o2) -> o1 - o2
│   └── Descending : (o1, o2) -> o2 - o1
│
├── Java 8+ API
│   ├── Comparator.comparing(KeyExtractor)
│   ├── .reversed()
│   └── .thenComparing(KeyExtractor)
│
└── Apply via
    ├── list.sort(comparator)
    └── Collections.sort(list, comparator)
```