## Module 2: Project Introduction: Game Of Thrones DataSet

In this module, you will import game of thrones data from my old node project into this new clojure project. Data for the project is found [here](https://github.com/vishalgautamm/graphQLofFireAndIce/tree/master/src/data)


### 2.1: Import Data

Go to this [link](https://github.com/vishalgautamm/graphQLofFireAndIce/tree/master/src/data) and download `books.json`, `characters.json` and `houses.json` json files and save it inside `resources/data` directory.

```
> mkdir resources/data

```

Now that we have downloaded all the data, it's time to funny understand it.


### 2.2 Understanding Data Part 1: `books.json`

Lets start by importing our first dependency - `clojure.data.json`

```clj
(ns user
  (:require [clojure.data.json :as json]))
```

We will use `read-str` function to read the books


```clj
(ns user
  (:require [clojure.data.json :as json]))

;;
;; 1.1 books
(def books (json/read-str (slurp "resources/data/books.json")))

```

Currently the keys are in string, we can use `:key-fn` property to convert into keyword. Lets import `clojure.pprint/pprint` function to see the shape of books

```clj
(ns user
  (:require [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]))

;;
;; 1.1 books
(def books (->  (slurp "resources/data/books.json")
                (json/read-str :key-fn keyword)))
(comment
 (pprint books))

[{:Id 1,
  :NumberOfPages 694,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "1996-08-01T00:00:00",
  :Name "A Game of Thrones",
  :ISBN "978-0553103540",
  :Authors ["George R. R. Martin"],
  :FollowedBy 2,
  :Country "United States",
  :PrecededById nil}
 {:Id 2,
  :NumberOfPages 768,
  :MediaType "Hardback",
  :Publisher "Bantam Books",
  :ReleaseDate "1999-02-02T00:00:00",
  :Name "A Clash of Kings",
  :ISBN "978-0553108033",
  :Authors ["George R. R. Martin"],
  :FollowedBy 3,
  :Country "United States",
  :PrecededById 1}
 {:Id 3,
  :NumberOfPages 992,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "2000-10-31T00:00:00",
  :Name "A Storm of Swords",
  :ISBN "978-0553106633",
  :Authors ["George R. R. Martin"],
  :FollowedBy 5,
  :Country "United States",
  :PrecededById 2}
 {:Id 4,
  :NumberOfPages 164,
  :MediaType "Graphic Novel",
  :Publisher "Dabel Brothers Publishing",
  :ReleaseDate "2005-03-09T00:00:00",
  :Name "The Hedge Knight",
  :ISBN "978-0976401100",
  :Authors ["George R. R. Martin"],
  :FollowedBy 6,
  :Country "United States",
  :PrecededById nil}
 {:Id 5,
  :NumberOfPages 784,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "2005-11-08T00:00:00",
  :Name "A Feast for Crows",
  :ISBN "978-0553801507",
  :Authors ["George R. R. Martin"],
  :FollowedBy 8,
  :Country "United Status",
  :PrecededById 3}
 {:Id 6,
  :NumberOfPages 152,
  :MediaType "Hardcover",
  :Publisher "Marvel",
  :ReleaseDate "2008-06-18T00:00:00",
  :Name "The Sworn Sword",
  :ISBN "978-0785126508",
  :Authors ["George R. R. Martin"],
  :FollowedBy 7,
  :Country "United States",
  :PrecededById 4}
 {:Id 7,
  :NumberOfPages 416,
  :MediaType "Paperback",
  :Publisher "Tor Fantasy",
  :ReleaseDate "2011-03-29T00:00:00",
  :Name "The Mystery Knight",
  :ISBN "978-0765360267",
  :Authors ["George R. R. Martin"],
  :FollowedBy nil,
  :Country "United States",
  :PrecededById 6}
 {:Id 8,
  :NumberOfPages 1040,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "2011-07-12T00:00:00",
  :Name "A Dance with Dragons",
  :ISBN "978-0553801477",
  :Authors ["George R. R. Martin"],
  :FollowedBy nil,
  :Country "United States",
  :PrecededById 5}
 {:Id 9,
  :NumberOfPages 784,
  :MediaType "Hardcover",
  :Publisher "Tor Books",
  :ReleaseDate "2013-12-03T00:00:00",
  :Name "The Princess and the Queen",
  :ISBN "978-0765332066",
  :Authors ["George R. R. Martin"],
  :FollowedBy nil,
  :Country "United States",
  :PrecededById nil}
 {:Id 10,
  :NumberOfPages 832,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "2014-06-17T00:00:00",
  :Name "The Rogue Prince",
  :ISBN "978-0345537263",
  :Authors ["George R. R. Martin"],
  :FollowedBy nil,
  :Country "United States",
  :PrecededById nil}
 {:Id 11,
  :NumberOfPages 336,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "2014-10-28T00:00:00",
  :Name "The World of Ice and Fire",
  :ISBN "978-0553805444",
  :Authors ["Elio Garcia" "Linda Antonsson" "George R. R. Martin"],
  :FollowedBy nil,
  :Country "United States",
  :PrecededById nil}
 {:Id 12,
  :NumberOfPages 368,
  :MediaType "Hardcover",
  :Publisher "Bantam Books",
  :ReleaseDate "2015-10-06T00:00:00",
  :Name "A Knight of the Seven Kingdoms",
  :ISBN "978-0345533487",
  :Authors ["George R. R. Martin"],
  :FollowedBy nil,
  :Country "United States",
  :PrecededById nil}]

```

To inspect a single book, we can use `first` function.

Pro Tip: Always use `pprint` function - it outputs a readable data

```clj
(comment
   (pprint (first books))
;;
{:Id 1,
 :NumberOfPages 694,
 :MediaType "Hardcover",
 :Publisher "Bantam Books",
 :ReleaseDate "1996-08-01T00:00:00",
 :Name "A Game of Thrones",
 :ISBN "978-0553103540",
 :Authors ["George R. R. Martin"],
 :FollowedBy 2,
 :Country "United States",
 :PrecededById nil}

```
We see that the book has 11 properties (count (first books)). We see that the name of the book
is "A Game of Thrones" and it has PrecededById of nil. Which means this is in fact the first book.
It does have FollowedBy value of 2, indicating the next book's ID. It also contains the name of the publisher, media type, isbn value and country.

**Exercise: Explore the data for characters and houses and answer the following question**
- What is the name of the 50th house
- How many characeters are there
- What is the Character Id of "Tyrion Lannister"
