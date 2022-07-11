# BudgetFixture

You can read more about this project 
[on my blog](https://antoniosbarotsis.github.io/Blog/posts/budgetfixture/)

This is a proof of concept clone of a great .NET package, 
[AutoFixture](https://github.com/AutoFixture/AutoFixture).

TLDR; it allows a tester to easily generate instances of a class involved
in their testing.

## How do I use it?

I decided to **not** publish this as it would need some effort to move it
from this "proof of concept" stage to a proper library. I've also not been
using much Java lately.

If you want to use this in an Intellij project then 

1. Generate the JAR file in this project by running

   ```bash
   mvn clean package
   ```

2. Open your new Intellij project and tap `Ctrl + Alt + Shift + S`
3. Go to `Libraries > New Project Library (+) > Java` and find the JAR file you just created

As for the actual code, *I talk more about it in my blog post*, but as a TLDR again:

- Create your class/record, let's create a `Person` record here

  ```java
  // Person.java
  public record Person(UUID id, String name) {}
  ```

- Create the 2 generators

  ```java
  // UuidGenerator.java
  public class UuidGenerator extends Generator<UUID> {
      @Override
      public UUID call() {
          return UUID.randomUUID();
      }
  }

  // StringGenerator.java
  public class StringGenerator extends Generator<String> {
      @Override
      public String call() {
          return "Random string";
      }
  }
  ```

- Go back to your Main class and add the following

  ```java
  // Main.java
  public class Main {
      public static void main(String[] args) {
          Fixture.registerGenerators();

          var test = Fixture.Generate(Person.class);
          System.out.println(test);
      }
  }
  ```

- If you hit `Run` you should see something like the following:

  ```
  Person[id=fe4c38b1-aefc-4f6d-a60c-ca4918a3ad79, name=Random string]
  ```
