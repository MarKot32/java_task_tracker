# Task Tracker – Konsolowy System Zarządzania Zadaniami

Projekt zaliczeniowy z języka Java. Aplikacja konsolowa do zarządzania zadaniami
w zespole, oparta wyłącznie na kolekcjach z Java Collections Framework
(bez bazy danych – cały stan trzymany w pamięci).

## Wymagania

- Java 17+
- Maven 3.x (opcjonalnie – projekt można też skompilować ręcznie, patrz niżej)

## Uruchomienie

### IntelliJ IDEA

1. Rozpakuj archiwum.
2. `File → Open...` i wskaż folder `task-tracker` (zawiera `pom.xml`, IntelliJ
   sam wykryje projekt Maven).
3. Uruchom klasę `pl.tasktracker.Main`.

### Z linii poleceń (Maven)

```bash
mvn package
java -jar target/task-tracker.jar
```

### Bez Mavena (czysty javac)

```bash
cd task-tracker
find src -name "*.java" > sources.txt
javac -d target/classes @sources.txt
java -cp target/classes pl.tasktracker.Main
```

## Struktura projektu

```
src/main/java/pl/tasktracker/
├── Main.java               – punkt wejścia, składanie zależności
├── model/                  – encje: User, Task, Priority, Status
├── repository/             – warstwa przechowywania danych (kolekcje)
├── service/                – logika biznesowa, walidacja, wyjątki
└── ui/                     – menu konsolowe (Scanner)
```

Architektura trójwarstwowa: **UI → Service → Repository**, każda warstwa
korzysta tylko z warstwy poniżej.

## Funkcjonalności

- Zarządzanie użytkownikami (dodawanie, lista, unikalny login)
- Zarządzanie zadaniami (dodawanie, zmiana statusu, tagi)
- Przypisywanie zadań do użytkowników
- Wyszukiwanie zadania po ID – O(1)
- Grupowanie zadań po statusie (TODO / IN_PROGRESS / DONE)
- Lista wszystkich unikalnych tagów, zawsze posortowana alfabetycznie
- Historia operacji w systemie (chronologicznie)

## Zastosowane kolekcje – skrót

| Kolekcja | Zastosowanie |
|---|---|
| `HashMap<String, Task>` | wyszukiwanie zadania po ID w czasie O(1) |
| `HashMap<String, User>` + `HashSet<String>` | wyszukiwanie i unikalność loginów |
| `HashMap<User, List<Task>>` | przypisania zadań do użytkowników |
| `EnumMap<Status, List<Task>>` | grupowanie zadań po statusie |
| `HashSet<String>` | unikalne tagi w ramach jednego zadania |
| `TreeSet<String>` | globalny zbiór tagów, zawsze alfabetycznie |
| `ArrayList<String>` | chronologiczna historia operacji |

Szersze uzasadnienie wyboru każdej kolekcji znajduje się jako komentarze
javadoc w klasach w pakiecie `repository`.

## Obsługa błędów

Błędy logiki biznesowej (np. nieistniejące ID, zajęty login) sygnalizowane są
wyjątkiem `TaskTrackerException` i przechwytywane w warstwie UI, która
wyświetla czytelny komunikat bez przerywania działania programu.
